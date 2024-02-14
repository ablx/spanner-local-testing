package dev.verbosemode.spannerlocaltesting.persistence

import com.google.cloud.spanner.Database
import com.google.cloud.spanner.Instance
import com.google.cloud.spanner.InstanceConfigId
import com.google.cloud.spanner.InstanceId
import com.google.cloud.spanner.InstanceInfo
import com.google.cloud.spanner.Spanner
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.SpannerEmulatorContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@SpringBootTest
@ActiveProfiles("emulator")
@Testcontainers
internal class WithEmulatorTest {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var spanner: Spanner

    @Value("\${spring.cloud.gcp.spanner.instance-id}")
    private lateinit var instanceId: String

    @Value("\${spring.cloud.gcp.spanner.database}")
    private lateinit var databaseId: String

    @Value("\${spring.cloud.gcp.project-id}")
    private lateinit var projectId: String


    @Test
    fun `should work with emulator`() {
        // given
        val database = createDatabase()
        createUserTable(database)
        val entity = UserEntity("id", "name", "email")

        // when
        val actual = userRepository.save(entity)
        val read = userRepository.findById(entity.id)

        // then
        assert(actual == entity)
        assert(actual == read.get())
    }

    @Test
    fun `should fail on alter index with stored columns`() {
        // given
        val database = createDatabase()
        createUserTable(database)

        // when
        executeDDL(database, "failing-index-change.sql")

        // then
    }

    private fun createInstance(): Instance {
        val instanceId = InstanceId.of(projectId, instanceId)
        val instanceInfo = InstanceInfo.newBuilder(instanceId)
            .setDisplayName("Test Instance")
            .setInstanceConfigId(
                InstanceConfigId.of(
                    projectId,
                    "config",
                ),
            )
            .build()
        return spanner.instanceAdminClient.createInstance(instanceInfo).get()


    }

    private fun createDatabase(): Database {
        createInstance()
        val databaseAdminClient = spanner.databaseAdminClient
        val databaseInfo = databaseAdminClient.createDatabase(
            instanceId,
            databaseId,
            emptyList()
        ).get()
        return databaseInfo

    }

    private fun executeDDL(database: Database, fileName: String) {
        val ddl = javaClass.getResource("/db/$fileName").readText()
        database.updateDdl(listOf(ddl), "op").get()
    }

    private fun createUserTable(database: Database) = executeDDL(database, "user-ddl.sql")

    companion object {


        @JvmStatic
        @Container
        val spannerEmulator: SpannerEmulatorContainer =
            SpannerEmulatorContainer(
                DockerImageName.parse("gcr.io/cloud-spanner-emulator/emulator:latest"),
            )

        // Since we do not know the port of the emulator, we need to set it dynamically.
        @JvmStatic
        @DynamicPropertySource
        @Suppress("unused")
        fun emulatorProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.cloud.gcp.spanner.emulator-host") {
                "http://${spannerEmulator.emulatorGrpcEndpoint}"
            }
        }
    }
}
package dev.verbosemode.spannerlocaltesting.persistence

import com.google.cloud.spring.data.spanner.repository.SpannerRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : SpannerRepository<UserEntity, String>
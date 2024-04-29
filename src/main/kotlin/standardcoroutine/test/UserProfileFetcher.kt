package standardcoroutine.test


class UserProfileFetcher(
    private val userNameRepository: UserNameRepository,
    private val userPhoneNumberRepository: UserPhoneNumberRepository,
) {
    fun getUserProfileById(id: String): UserProfile {
        val userName = userNameRepository.getById(id)
        val userPhoneNumber = userPhoneNumberRepository.getById(id)
        return UserProfile(id, userName, userPhoneNumber)
    }
}
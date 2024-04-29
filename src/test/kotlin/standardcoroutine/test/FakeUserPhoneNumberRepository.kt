package standardcoroutine.test

class FakeUserPhoneNumberRepository : UserPhoneNumberRepository {
    private val userPhoneNumberMap = mutableMapOf<String, String>()

    override fun save(id: String, phoneNumber: String) {
        userPhoneNumberMap[id] = phoneNumber
    }

    override fun getById(id: String): String {
        return userPhoneNumberMap[id] ?: ""
    }
}
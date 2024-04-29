package standardcoroutine.test

interface UserPhoneNumberRepository {
    fun save(id: String, phoneNumber: String)
    fun getById(id: String): String
}
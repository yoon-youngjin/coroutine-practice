package standardcoroutine.test

interface UserNameRepository {
    fun save(id: String, name: String)
    fun getById(id: String): String
}
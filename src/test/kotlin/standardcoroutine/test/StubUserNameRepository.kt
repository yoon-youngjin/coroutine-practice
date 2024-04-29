package standardcoroutine.test

class StubUserNameRepository(
    private val userNameMap: Map<String, String>
) : UserNameRepository {
    override fun save(id: String, name: String) {
    }

    override fun getById(id: String): String {
        return userNameMap[id] ?: ""
    }
}
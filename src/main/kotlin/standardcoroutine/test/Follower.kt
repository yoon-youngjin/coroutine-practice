package standardcoroutine.test

sealed class Follower(
    private val id: String,
    private val name: String,
) {
    data class OfficialAccount(
        val id: String,
        val name: String,
    ) : Follower(id, name)

    data class PersonAccount(
        val id: String,
        val name: String,
    ) : Follower(id, name)
}
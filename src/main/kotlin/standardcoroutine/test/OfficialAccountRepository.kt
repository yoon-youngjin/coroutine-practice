package standardcoroutine.test

interface OfficialAccountRepository {
    suspend fun searchByName(name: String): Array<Follower.OfficialAccount>
}
package standardcoroutine.test

import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class UserProfileFetcherTest {
    private val userProfileFetcher = UserProfileFetcher(
        userNameRepository = StubUserNameRepository(
            userNameMap = mapOf(
                "0x1111" to "홍길동",
                "0x2222" to "윤영진",
            )
        ),
        userPhoneNumberRepository = FakeUserPhoneNumberRepository().apply {
            save("0x1111", "010-xxxx-xxxx")
        },
    )

    @Test
    fun `UserNameRepository가 반환하는 이름이 홍길동이면 UserProfileFetcher에서 UserProfile을 가져왔을 때 이름이 홍길동이어야 한다`() {
        // when
        val userProfile = userProfileFetcher.getUserProfileById("0x1111")

        // then
        userProfile.name shouldBe "홍길동"
    }

    @Test
    fun `UserPhoneNumberRepository에 휴대폰 번호가 저장돼 있으면, UserProfile을 가져왔을 때 해당 휴대폰 번호가 반환돼야 한다`() {
        // when
        val userProfile = userProfileFetcher.getUserProfileById("0x1111")

        // then
        userProfile.phoneNumber shouldBe "010-xxxx-xxxx"
    }
}
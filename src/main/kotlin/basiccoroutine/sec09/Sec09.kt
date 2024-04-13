package basiccoroutine.sec09

import kotlinx.coroutines.delay

suspend fun main() {
    val service = UserService()
    println(service.findUser(1L, null))
}

interface Continuation {
    suspend fun resumeWith(data: Any?)
}

class UserService {
    private val userProfileRepository = UserProfileRepository()
    private val userImageRepository = UserImageRepository()

    private abstract class FindUserContinuation : Continuation {
        var label = 0
        var profile: Profile? = null
        var image: Image? = null
    }

    suspend fun findUser(userId: Long, continuation: Continuation?): UserDto {
        // state machine의 약자, 라벨을 기준으로 상태를 관리하므로 sm
        val sm = continuation as? FindUserContinuation ?: object : FindUserContinuation() {
            override suspend fun resumeWith(data: Any?) {
                when (label) {
                    0 -> {
                        profile = data as Profile
                        label = 1
                    }

                    1 -> {
                        image = data as Image
                        label = 2
                    }
                }
                findUser(userId, this)
            }

        }


        when (sm.label) {
            0 -> {
                println("프로필을 가져오겠습니다")
                userProfileRepository.findProfile(userId, sm)
            }

            1 -> {
                println("이미지를 가져오겠습니다")
                userImageRepository.findImage(sm.profile!!, sm)
            }
        }
        return UserDto(sm.profile!!, sm.image!!)
    }
}


data class UserDto(
    val profile: Profile,
    val image: Image,
)

class UserProfileRepository {
    suspend fun findProfile(userId: Long, continuation: Continuation) {
        // userId를 통해 Profile 찾는 쿼리
        delay(100L)
        return continuation.resumeWith(Profile())
    }
}

class Profile
class UserImageRepository {
    suspend fun findImage(profile: Profile, continuation: Continuation) {
        // profile을 통해 Image를 찾는 쿼리
        delay(100L)
        return continuation.resumeWith(Image())
    }
}

class Image
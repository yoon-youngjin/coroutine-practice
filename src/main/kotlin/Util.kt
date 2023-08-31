

fun printWithThread(str: Any) { // 현재 쓰레드와 함께 출력
    println("[${Thread.currentThread().name}] $str")
}
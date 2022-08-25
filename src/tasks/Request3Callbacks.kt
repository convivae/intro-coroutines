package tasks

import contributors.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import java.util.concurrent.CountDownLatch

// solution: https://play.kotlinlang.org/hands-on/Introduction%20to%20Coroutines%20and%20Channels/03_UsingCallbacks

fun loadContributorsCallbacks(service: GitHubService, req: RequestData, updateResults: (List<User>) -> Unit) {
    service.getOrgReposCall(req.org).onResponse { responseRepos ->
        logRepos(req, responseRepos)
        val repos = responseRepos.bodyList()
        val allUsers = Collections.synchronizedList(mutableListOf<User>())

        // 方法 1：使用 AtomicInteger
//        val numberOfProcessed = AtomicInteger()
//        for (repo in repos) {
//            service.getRepoContributorsCall(req.org, repo.name).onResponse { responseUsers ->
//                logUsers(repo, responseUsers)
//                val users = responseUsers.bodyList()
//                allUsers += users
//                if(numberOfProcessed.incrementAndGet() == repos.size){
//                    updateResults(allUsers.aggregate())
//                }
//            }
//        }

        // 方法 2：使用 CountDownLatch
        // 每次调用后对 counter 减 1 直至为 0
        val countDownLatch = CountDownLatch(repos.size)
        for (repo in repos) {
            service.getRepoContributorsCall(req.org, repo.name).onResponse { responseUsers ->
                logUsers(repo, responseUsers)
                val users = responseUsers.bodyList()
                allUsers += users
                countDownLatch.countDown()
            }
        }
        countDownLatch.await()
        updateResults(allUsers.aggregate())

        // TODO: Why this code doesn't work? How to fix that?
        // 因为调用下面这句话的时候数据还未返回，allUsers 是空的
        // updateResults(allUsers.aggregate())
    }
}

inline fun <T> Call<T>.onResponse(crossinline callback: (Response<T>) -> Unit) {
    enqueue(object : Callback<T> {
        override fun onResponse(call: Call<T>, response: Response<T>) {
            callback(response)
        }

        override fun onFailure(call: Call<T>, t: Throwable) {
            log.error("Call failed", t)
        }
    })
}

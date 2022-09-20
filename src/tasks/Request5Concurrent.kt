package tasks

import contributors.*
import kotlinx.coroutines.*

/**
 * 运行代码并检查日志。您可以看到所有协程仍然在主 UI 线程上运行，因为尚未使用多线程。但是并发运行协程已经有好处了。
 *
 * 要更改此代码以在来自公共线程池的不同线程上运行“贡献者”协程，请指定Dispatchers.Default
 * 再次运行程序。在日志中，您可以看到每个协程都可以在线程池中的一个线程上启动并在另一个线程上恢复：
 * 要仅在主 UI 线程上运行协程，请指定 Dispatchers.Main 为参数
 *
 * 一般而言，将async中的参数作为参数传进来是一种更好的写法，可以自定义调度
 */
suspend fun loadContributorsConcurrent(service: GitHubService, req: RequestData): List<User> = coroutineScope {
    val repos = service
        .getOrgRepos(req.org)
        .also { logRepos(req, it) } // 错误也会在这里打印
        .body() ?: emptyList()

    // 无需再调用 flatMap，map 中存储的是 Deferred 的对象
    val deferreds: List<Deferred<List<User>>> = repos.map { repo ->
        async(Dispatchers.Default) {
            // load contributors for each repo
            log("starting loading for ${repo.name}")
            service
                .getRepoContributors(req.org, repo.name)
                .also { logUsers(repo, it) }
                .bodyList()
        }
    }
    deferreds.awaitAll().flatten().aggregate()
}
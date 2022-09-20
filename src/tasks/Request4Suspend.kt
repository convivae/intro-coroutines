package tasks

import contributors.*

/**
 * loadContributorsSuspend 需要定义为 suspend 函数
 * execute 函数用来返回 Response 不再需要，因为直接返回了 Response
 *
 * 带有挂起函数的代码看起来类似于“阻塞”(block)版本。
 * 与阻塞版本的主要区别在于，协程不是阻塞线程，而是挂起：
 */
suspend fun loadContributorsSuspend(service: GitHubService, req: RequestData): List<User> {
    val repos = service
        .getOrgRepos(req.org)
        .also { logRepos(req, it) } // 错误也会在这里打印
        .body() ?: emptyList()

    return repos.flatMap { repo ->
        service
            .getRepoContributors(req.org, repo.name)
            .also { logUsers(repo, it) }
            .bodyList()
    }.aggregate()
}
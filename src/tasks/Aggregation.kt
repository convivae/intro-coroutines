package tasks

import contributors.User

/*
TODO: Write aggregation code.

 In the initial list each user is present several times, once for each
 repository he or she contributed to.
 Merge duplications: each user should be present only once in the resulting list
 with the total value of contributions for all the repositories.
 Users should be sorted in a descending order by their contributions.

 The corresponding test can be found in test/tasks/AggregationKtTest.kt.
 You can use 'Navigate | Test' menu action (note the shortcut) to navigate to the test.
*/

/**
 * 1. 按照 用户名(login) 合并 contributions 的数量
 * 2. 按 contributions 数量进行降序排序
 *
 * Test: AggregationKtTest.kt
 */
fun List<User>.aggregate(): List<User> {
    // 方法一 groupBy
//    return this.groupBy { it.login }
//        .map { (login, group) -> User(login, group.sumOf { it.contributions }) }
//        .sortedByDescending { it.contributions }

    // 方法二 groupingBy
    return this.groupingBy { it.login }
        .aggregate<User, String, Int> { _, accumulator, element, _ ->
            element.contributions + (accumulator ?: 0)
        }
        .map { (k, v) -> User(k, v) }
        .sortedByDescending { it.contributions }
}
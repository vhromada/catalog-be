package com.github.vhromada.catalog

import org.springframework.beans.factory.getBeanProvider
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.queryForObject
import org.springframework.test.context.TestContext
import org.springframework.test.context.support.AbstractTestExecutionListener
import java.util.concurrent.ConcurrentHashMap
import javax.sql.DataSource

/**
 * A class represents listener, which restores values of database sequences after each test method.
 * Sequences are not transactional, so rollback doesn't revert them. Without restoring, identifiers of rows created in a test depend on tests executed before.
 *
 * @author Vladimir Hromada
 */
class SequenceResetTestExecutionListener : AbstractTestExecutionListener() {

    /**
     * Values of sequences before test method by test class and method
     */
    private val snapshots = ConcurrentHashMap<String, Map<String, Long?>>()

    override fun getOrder(): Int {
        return LOWEST_PRECEDENCE - 1000
    }

    override fun beforeTestMethod(testContext: TestContext) {
        val template = getTemplate(testContext = testContext) ?: return
        snapshots[getKey(testContext = testContext)] = template.query("SELECT sequencename, last_value FROM pg_sequences") { rs, _ ->
            val value = rs.getLong("last_value")
            rs.getString("sequencename") to if (rs.wasNull()) null else value
        }.toMap()
    }

    override fun afterTestMethod(testContext: TestContext) {
        val snapshot = snapshots.remove(getKey(testContext = testContext)) ?: return
        val template = getTemplate(testContext = testContext) ?: return
        snapshot.forEach { (name, value) ->
            if (value == null) {
                template.queryForObject<Long>("SELECT setval(?::regclass, 1, false)", name)
            } else {
                template.queryForObject<Long>("SELECT setval(?::regclass, ?, true)", name, value)
            }
        }
    }

    /**
     * Returns JDBC template.
     *
     * @param testContext test context
     * @return JDBC template or null if context doesn't contain data source
     */
    private fun getTemplate(testContext: TestContext): JdbcTemplate? {
        val dataSource = testContext.applicationContext.getBeanProvider<DataSource>().ifAvailable ?: return null
        return JdbcTemplate(dataSource)
    }

    /**
     * Returns key of test method.
     *
     * @param testContext test context
     * @return key of test method
     */
    private fun getKey(testContext: TestContext): String {
        return "${testContext.testClass.name}#${testContext.testMethod.name}"
    }

}

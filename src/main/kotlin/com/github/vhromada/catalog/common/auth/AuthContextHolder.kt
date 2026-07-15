package com.github.vhromada.catalog.common.auth

/**
 * A class represents holder for auth context.
 *
 * @author Vladimir Hromada
 */
object AuthContextHolder {

    /**
     * Thread local holder for auth context
     */
    private val CONTEXT = ThreadLocal<AuthContext>()

    /**
     * Inits auth context.
     *
     * @param authContext auth context
     */
    fun init(authContext: AuthContext) {
        CONTEXT.set(authContext)
    }

    /**
     * Returns auth context.
     *
     * @return auth context
     */
    fun get(): AuthContext {
        return CONTEXT.get() ?: AuthContext().also { CONTEXT.set(it) }
    }

    /**
     * Clears data.
     */
    fun clear() {
        CONTEXT.remove()
    }

}

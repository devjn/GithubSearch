package com.github.devjn.githubsearch.database

interface ISQLiteContentValues {

    /**
     * Adds a value to the set.

     * @param key the name of the value to put
     * *
     * @param value the data for the value to put
     */
    fun put(key: String, value: String)

    /**
     * Adds a value to the set.

     * @param key the name of the value to put
     * *
     * @param value the data for the value to put
     */
    fun put(key: String, value: Byte?)

    /**
     * Adds a value to the set.

     * @param key the name of the value to put
     * *
     * @param value the data for the value to put
     */
    fun put(key: String, value: Short?)

    /**
     * Adds a value to the set.

     * @param key the name of the value to put
     * *
     * @param value the data for the value to put
     */
    fun put(key: String, value: Int?)

    /**
     * Adds a value to the set.

     * @param key the name of the value to put
     * *
     * @param value the data for the value to put
     */
    fun put(key: String, value: Long?)

    /**
     * Adds a value to the set.

     * @param key the name of the value to put
     * *
     * @param value the data for the value to put
     */
    fun put(key: String, value: Float?)

    /**
     * Adds a value to the set.

     * @param key the name of the value to put
     * *
     * @param value the data for the value to put
     */
    fun put(key: String, value: Double?)

    /**
     * Adds a value to the set.

     * @param key the name of the value to put
     * *
     * @param value the data for the value to put
     */
    fun put(key: String, value: Boolean?)

    fun size(): Int

    fun keySet(): Set<String>

    operator fun get(colName: String): Any?

}
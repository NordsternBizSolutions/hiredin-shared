package com.nordstern.hiredin.shared.utils

object HtmlParser {
    private val tagRegex = Regex("<[^>]+>")

    fun stripTags(html: String): String = tagRegex.replace(html, "").trim()

    fun extractText(html: String): String =
        html.replace(Regex("""<script[^>]*>[\s\S]*?</script>""", RegexOption.IGNORE_CASE), "")
            .replace(Regex("""<style[^>]*>[\s\S]*?</style>""", RegexOption.IGNORE_CASE), "")
            .let { stripTags(it) }
            .replace(Regex("""\s+"""), " ")
            .trim()
}

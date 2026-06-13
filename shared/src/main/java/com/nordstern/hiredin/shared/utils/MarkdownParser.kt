package com.nordstern.hiredin.shared.utils

object MarkdownParser {
    fun toPlainText(markdown: String): String =
        markdown
            .replace(Regex("""^#+\s+""", RegexOption.MULTILINE), "")
            .replace(Regex("""\*\*(.+?)\*\*"""), "$1")
            .replace(Regex("""\*(.+?)\*"""), "$1")
            .replace(Regex("""\[(.+?)\]\(.+?\)"""), "$1")
            .replace(Regex("""`(.+?)`"""), "$1")
            .trim()

    fun extractHeadings(markdown: String): List<String> =
        Regex("""^#{1,6}\s+(.+)$""", RegexOption.MULTILINE)
            .findAll(markdown)
            .map { it.groupValues[1] }
            .toList()
}

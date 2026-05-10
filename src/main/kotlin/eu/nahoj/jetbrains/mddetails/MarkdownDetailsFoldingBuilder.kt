package eu.nahoj.jetbrains.mddetails

import com.intellij.lang.ASTNode
import com.intellij.lang.folding.CustomFoldingBuilder
import com.intellij.lang.folding.FoldingDescriptor
import com.intellij.openapi.editor.Document
import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.intellij.plugins.markdown.lang.MarkdownLanguage
import java.util.regex.Pattern

/**
 * Collapses every <details>...</details> block in Markdown documents by default.
 * Placeholder shows the <summary> text if present, otherwise a generic label.
 *
 * Notes:
 * - This is text-range based and does not depend on Markdown PSI internals,
 *   which keeps it robust across IDE versions.
 * - If you want to respect the HTML 'open' attribute (to keep some expanded),
 *   flip RESPECT_OPEN_ATTRIBUTE to true.
 */
class MarkdownDetailsFoldingBuilder : CustomFoldingBuilder(), DumbAware {

    private val detailsBlockPattern: Pattern =
        Pattern.compile("(?is)<details\\b[^>]*>.*?</details\\s*>")

    private val summaryPattern: Pattern =
        Pattern.compile("(?is)<summary\\b[^>]*>(.*?)</summary\\s*>")

    // Set to true if you want to keep <details open> expanded by default.
    private val RESPECT_OPEN_ATTRIBUTE = false

    override fun buildLanguageFoldRegions(
        descriptors: MutableList<FoldingDescriptor>,
        root: PsiElement,
        document: Document,
        quick: Boolean
    ) {
        if (!isMarkdownFile(root.containingFile)) return

        val text = document.text
        val matcher = detailsBlockPattern.matcher(text)
        while (matcher.find()) {
            val start = matcher.start()
            val end = matcher.end()

            if (RESPECT_OPEN_ATTRIBUTE) {
                val openTag = text.substring(matcher.start(), minOf(matcher.start() + 200, text.length))
                if (openTag.contains(Regex("(?i)<details\\b[^>]*\\bopen\\b"))) {
                    // Skip adding a folding region so it stays expanded.
                    continue
                }
            }
            // Attach descriptors to the file's root node; CustomFoldingBuilder lets us compute
            // per-range placeholders via getLanguagePlaceholderText(...)
            descriptors.add(FoldingDescriptor(root.node, TextRange(start, end)))
        }
    }

    override fun getLanguagePlaceholderText(node: ASTNode, range: TextRange): String {
        val file = node.psi.containingFile ?: return "<details>"
        val doc = file.viewProvider.document ?: return "<details>…</details>"
        val block = safeSubSequence(doc, range).toString()

        val m = summaryPattern.matcher(block)
        val summary = if (m.find()) {
            // Collapse whitespace for a clean one-line placeholder
            m.group(1).replace(Regex("\\s+"), " ").trim()
        } else null

        return if (!summary.isNullOrEmpty()) "▶ $summary" else "<details>…</details>"
    }

    override fun isRegionCollapsedByDefault(node: ASTNode): Boolean {
        // We only create regions for <details> blocks, and we want them collapsed by default.
        return true
    }

    private fun isMarkdownFile(file: PsiFile?): Boolean {
        return file != null && file.language.isKindOf(MarkdownLanguage.INSTANCE)
    }

    private fun safeSubSequence(doc: Document, range: TextRange): CharSequence {
        val start = range.startOffset.coerceIn(0, doc.textLength)
        val end = range.endOffset.coerceIn(0, doc.textLength)
        return doc.charsSequence.subSequence(start, end)
    }
}

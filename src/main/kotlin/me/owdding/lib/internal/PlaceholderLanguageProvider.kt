package me.owdding.lib.internal

import eu.pb4.placeholders.api.ParserContext
import eu.pb4.placeholders.api.arguments.StringArgs
import eu.pb4.placeholders.api.node.DynamicTextNode
import eu.pb4.placeholders.api.node.TextNode
import eu.pb4.placeholders.api.node.parent.ColorNode
import eu.pb4.placeholders.api.parsers.TagLikeParser
import eu.pb4.placeholders.api.parsers.tag.TagRegistry
import eu.pb4.placeholders.impl.textparser.SingleTagLikeParser
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TextColor
import org.apache.commons.lang3.StringUtils
import java.util.function.Function

object PlaceholderLanguageProvider : TagLikeParser.Provider {

    private val KEY = ParserContext.Key.of<Function<String, Component>>("meowdding:translation_args")
    private var PARSER = SingleTagLikeParser(TagLikeParser.TAGS, PlaceholderLanguageProvider)

    override fun isValidTag(tag: String, context: TagLikeParser.Context): Boolean {
        return tag == "/*"
            || tag.startsWith("#")
            || TagRegistry.SAFE.getTag(tag) != null
            || StringUtils.isNumeric(tag)
            || tag == "/"
            || (tag.length > 1 && tag[0] == '/' && context.contains(tag.substring(1)))
            || (tag.length > 1 && tag[0] == ';' && context.contains(tag.substring(1)))
    }

    override fun handleTag(id: String, argument: String, context: TagLikeParser.Context) {
        if (id == "/" || id == "/" + context.peekId() || id == ";" + context.peekId()) {
            context.pop()
        } else if (id == "/*") {
            context.pop(context.size())
        } else if (id.length > 1 && id[0] == '/') {
            var s = id.substring(1)
            context.pop(s)
        } else if (id.length > 1 && id[0] == ';') {
            var s = id.substring(1)
            context.popOnly(s)
        } else if (id.startsWith("#")) {
            var text = TextColor.parseColor(id)
            if (text.result().isPresent) {
                context.push(id) { ColorNode(it, text.result().get()) }
            }
        } else if (StringUtils.isNumeric(id)) {
            context.addNode(DynamicTextNode.of(id, KEY))
        } else {
            var tag = TagRegistry.SAFE.getTag(id)!!

            var args = StringArgs.full(argument, ' ', ':')

            if (tag.selfContained()) {
                context.addNode(tag.nodeCreator().createTextNode(TextNode.array(), args, context.parser()))
            } else {
                context.push(id) { tag.nodeCreator().createTextNode(it, args, context.parser()) }
            }
        }
    }

    fun parse(text: String, args: Array<Any?>): Component = PARSER.parseText(text, ParserContext.of(
        KEY, Function { key ->
            var index = key.toIntOrNull()
            if (index != null && index in args.indices) {
                args[index] as? Component ?: Component.literal(args[index].toString())
            } else {
                Component.literal(key)
            }
        }
    ))
}

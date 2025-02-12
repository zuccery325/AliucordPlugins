package com.aliucord.plugins

import android.content.Context
import android.view.View
import com.aliucord.Utils
import com.aliucord.entities.Plugin
import com.aliucord.patcher.PinePatchFn
import com.aliucord.plugins.attachmentutils.AttachmentContextMenu
import com.aliucord.wrappers.messages.AttachmentWrapper
import com.discord.api.message.attachment.MessageAttachment
import com.discord.databinding.WidgetChatListAdapterItemAttachmentBinding
import com.discord.utilities.textprocessing.MessageRenderContext
import com.discord.widgets.chat.list.adapter.WidgetChatListAdapterItemAttachment

class AttachmentUtils : Plugin() {

    override fun getManifest() =
        Manifest().apply {
            authors = arrayOf(Manifest.Author("Xinto", 423915768191647755L))
            description = "Adds a context menu to attachments."
            version = "1.0.0"
            updateUrl = "https://raw.githubusercontent.com/X1nto/AliucordPlugins/builds/updater.json"
        }

    override fun start(context: Context?) {
        patcher.patch(
            WidgetChatListAdapterItemAttachment::class.java.getDeclaredMethod(
                "configureFileData",
                MessageAttachment::class.java,
                MessageRenderContext::class.java
            ),
            PinePatchFn { callFrame ->
                val thisObject = callFrame.thisObject as WidgetChatListAdapterItemAttachment
                val binding = thisObject::class.java
                    .getDeclaredField("binding")
                    .let {
                        it.isAccessible = true
                        it.get(thisObject) as WidgetChatListAdapterItemAttachmentBinding
                    }
                val messageAttachment = AttachmentWrapper(callFrame.args[0] as MessageAttachment)

                binding.d.setCopyUrlSheetListener(messageAttachment.url)
                binding.h.setCopyUrlSheetListener(messageAttachment.url)
            }
        )
    }

    override fun stop(context: Context?) {
        patcher.unpatchAll()
    }

    private fun View.setCopyUrlSheetListener(url: String) {
        setOnLongClickListener {
            AttachmentContextMenu
                .newInstance(url)
                .show(Utils.appActivity.supportFragmentManager, "Attachment Utils")
            return@setOnLongClickListener true
        }
    }

}
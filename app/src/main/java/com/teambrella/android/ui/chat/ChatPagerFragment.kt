package com.teambrella.android.ui.chat

import android.os.Bundle
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.teambrella.android.api.*
import com.teambrella.android.data.base.IDataPager
import com.teambrella.android.data.base.TeambrellaDataPagerFragment
import com.teambrella.android.ui.TeambrellaUser
import com.teambrella.android.ui.base.TeambrellaDataHostActivity
import java.util.*

class ChatPagerFragment : TeambrellaDataPagerFragment() {
    override fun createLoader(args: Bundle): IDataPager<JsonArray> {
        return KChatDataPagerLoader(args.getParcelable(TeambrellaDataPagerFragment.EXTRA_URI), TeambrellaUser.get(context).userId)
                .also {
                    (context as TeambrellaDataHostActivity).component.inject(it)
                }
    }

    fun addPendingMessage(postId: String, message: String, vote: Float) {

        val loader = pager as KChatDataPagerLoader

        val post = JsonObject().apply {
            userId = TeambrellaUser.get(context).userId
            text = message
            stringId = postId
            messageStatus = TeambrellaModel.PostStatus.POST_PENDING
            chatItemType = ChatItems.CHAT_ITEM_MY_MESSAGE
            val currentDate = Calendar.getInstance()
            added = currentDate.time.time

            if (vote >= 0) {
                val teammate = JsonObject()
                teammate.addProperty(TeambrellaModel.ATTR_DATA_VOTE, vote)
                add(TeambrellaModel.ATTR_DATA_ONE_TEAMMATE, teammate)
            }

            val lastDate = loader.getLastDate(true)
            isNextDay = KChatDataPagerLoader.isNextDay(lastDate, currentDate)
        }

        if (post.isNextDay == true) {
            loader.addAsNext(post.deepCopy().apply {
                chatItemType = ChatItems.CHAT_ITEM_DATE
            })
        }

        loader.addAsNext(post)
    }

    fun addPendingImage(postId: String, fileUri: String, ratio: Float) {
        val loader = pager as KChatDataPagerLoader
        val post = JsonObject().apply {
            userId = TeambrellaUser.get(context).userId
            stringId = postId
            messageStatus = TeambrellaModel.PostStatus.POST_PENDING
            imageIndex = 0
            chatItemType = ChatItems.CHAT_ITEM_MY_IMAGE
            val currentDate = Calendar.getInstance()
            added = currentDate.time.time

            val images = JsonArray()
            images.add(fileUri)
            add(TeambrellaModel.ATTR_DATA_LOCAL_IMAGES, images)
            val ratios = JsonArray()
            ratios.add(ratio)
            add(TeambrellaModel.ATTR_DATA_IMAGE_RATIOS, ratios)
            val lastDate = loader.getLastDate(true)
            isNextDay = KChatDataPagerLoader.isNextDay(lastDate, currentDate)

        }

        if (post.isNextDay == true) {
            loader.addAsNext(post.deepCopy().apply {
                chatItemType = ChatItems.CHAT_ITEM_DATE
            })
        }

        loader.addAsNext(post)

    }
}
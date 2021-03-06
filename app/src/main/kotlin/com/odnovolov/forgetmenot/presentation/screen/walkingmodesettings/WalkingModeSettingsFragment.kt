package com.odnovolov.forgetmenot.presentation.screen.walkingmodesettings

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.odnovolov.forgetmenot.R
import com.odnovolov.forgetmenot.presentation.common.base.BaseFragment
import com.odnovolov.forgetmenot.presentation.common.customview.ChoiceDialogCreator
import com.odnovolov.forgetmenot.presentation.common.customview.ChoiceDialogCreator.Item
import com.odnovolov.forgetmenot.presentation.common.customview.ChoiceDialogCreator.ItemAdapter
import com.odnovolov.forgetmenot.presentation.common.customview.ChoiceDialogCreator.ItemForm.AsRadioButton
import com.odnovolov.forgetmenot.presentation.common.firstBlocking
import com.odnovolov.forgetmenot.presentation.common.needToCloseDiScope
import com.odnovolov.forgetmenot.presentation.common.showActionBar
import com.odnovolov.forgetmenot.presentation.screen.walkingmodesettings.KeyGesture.*
import com.odnovolov.forgetmenot.presentation.screen.walkingmodesettings.KeyGestureAction.*
import com.odnovolov.forgetmenot.presentation.screen.walkingmodesettings.WalkingModeSettingsEvent.KeyGestureActionSelected
import kotlinx.android.synthetic.main.fragment_walking_mode_settings.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class WalkingModeSettingsFragment : BaseFragment() {
    init {
        WalkingModeSettingsDiScope.reopenIfClosed()
    }

    private lateinit var viewModel: WalkingModeSettingsViewModel
    private var controller: WalkingModeSettingsController? = null
    private lateinit var chooseKeyGestureActionDialog: Dialog
    private lateinit var chooseKeyGestureActionDialogAdapter: ItemAdapter
    private var activeRemappingKeyGesture: KeyGesture? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initDialog()
        return inflater.inflate(R.layout.fragment_walking_mode_settings, container, false)
    }

    private fun initDialog() {
        chooseKeyGestureActionDialog = ChoiceDialogCreator.create(
            context = requireContext(),
            itemForm = AsRadioButton,
            onItemClick = { item: Item ->
                item as KeyGestureActionItem
                chooseKeyGestureActionDialog.dismiss()
                controller?.dispatch(
                    KeyGestureActionSelected(
                        activeRemappingKeyGesture!!,
                        item.keyGestureAction
                    )
                )
                activeRemappingKeyGesture = null
            },
            takeAdapter = { chooseKeyGestureActionDialogAdapter = it }
        )
        dialogTimeCapsule.register("chooseKeyGestureActionDialog", chooseKeyGestureActionDialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewCoroutineScope!!.launch {
            val diScope = WalkingModeSettingsDiScope.getAsync() ?: return@launch
            controller = diScope.controller
            viewModel = diScope.viewModel
            setupView()
            observeViewModel()
        }
    }

    private fun setupView() {
        volumeUpSinglePressButton.setOnClickListener {
            activeRemappingKeyGesture = VOLUME_UP_SINGLE_PRESS
            showDialog()
        }
        volumeUpDoublePressButton.setOnClickListener {
            activeRemappingKeyGesture = VOLUME_UP_DOUBLE_PRESS
            showDialog()
        }
        volumeUpLongPressButton.setOnClickListener {
            activeRemappingKeyGesture = VOLUME_UP_LONG_PRESS
            showDialog()
        }
        volumeDownSinglePressButton.setOnClickListener {
            activeRemappingKeyGesture = VOLUME_DOWN_SINGLE_PRESS
            showDialog()
        }
        volumeDownDoublePressButton.setOnClickListener {
            activeRemappingKeyGesture = VOLUME_DOWN_DOUBLE_PRESS
            showDialog()
        }
        volumeDownLongPressButton.setOnClickListener {
            activeRemappingKeyGesture = VOLUME_DOWN_LONG_PRESS
            showDialog()
        }
    }

    private fun showDialog() {
        updateAdapterItems()
        updateDialogTitle()
        chooseKeyGestureActionDialog.show()
    }

    private fun updateAdapterItems() {
        val selectedKeyGestureAction: KeyGestureAction = with(viewModel) {
            when (activeRemappingKeyGesture!!) {
                VOLUME_UP_SINGLE_PRESS -> selectedVolumeUpSinglePressAction
                VOLUME_UP_DOUBLE_PRESS -> selectedVolumeUpDoublePressAction
                VOLUME_UP_LONG_PRESS -> selectedVolumeUpLongPressAction
                VOLUME_DOWN_SINGLE_PRESS -> selectedVolumeDownSinglePressAction
                VOLUME_DOWN_DOUBLE_PRESS -> selectedVolumeDownDoublePressAction
                VOLUME_DOWN_LONG_PRESS -> selectedVolumeDownLongPressAction
            }.firstBlocking()
        }
        val items: List<KeyGestureActionItem> = KeyGestureAction.values().map {
            KeyGestureActionItem(
                keyGestureAction = it,
                text = getString(getStringResIdOf(it)),
                isSelected = it === selectedKeyGestureAction
            )
        }
        chooseKeyGestureActionDialogAdapter.submitList(items)
    }

    private fun updateDialogTitle() {
        val resId = when (activeRemappingKeyGesture!!) {
            VOLUME_UP_SINGLE_PRESS -> R.string.text_single_press_title
            VOLUME_UP_DOUBLE_PRESS -> R.string.text_double_press_title
            VOLUME_UP_LONG_PRESS -> R.string.text_long_press_title
            VOLUME_DOWN_SINGLE_PRESS -> R.string.text_single_press_title
            VOLUME_DOWN_DOUBLE_PRESS -> R.string.text_double_press_title
            VOLUME_DOWN_LONG_PRESS -> R.string.text_long_press_title
        }
        chooseKeyGestureActionDialog.setTitle(resId)
    }

    private fun observeViewModel() {
        with(viewModel) {
            selectedVolumeUpSinglePressAction.observeBy(selectedVolumeUpSinglePressActionTextView)
            selectedVolumeUpDoublePressAction.observeBy(selectedVolumeUpDoublePressActionTextView)
            selectedVolumeUpLongPressAction.observeBy(selectedVolumeUpLongPressActionTextView)
            selectedVolumeDownSinglePressAction.observeBy(
                selectedVolumeDownSinglePressActionTextView
            )
            selectedVolumeDownDoublePressAction.observeBy(
                selectedVolumeDownDoublePressActionTextView
            )
            selectedVolumeDownLongPressAction.observeBy(selectedVolumeDownLongPressActionTextView)
        }
    }

    private fun Flow<KeyGestureAction>.observeBy(textView: TextView) {
        observe { keyGestureAction: KeyGestureAction ->
            val resId = getStringResIdOf(keyGestureAction)
            textView.setText(resId)
        }
    }

    private fun getStringResIdOf(keyGestureAction: KeyGestureAction) = when (keyGestureAction) {
        NO_ACTION -> R.string.key_gesture_action_no_action
        MOVE_TO_NEXT_CARD -> R.string.key_gesture_action_move_to_next_card
        MOVE_TO_PREVIOUS_CARD -> R.string.key_gesture_action_move_to_previous_card
        MARK_AS_REMEMBER -> R.string.key_gesture_action_mark_as_remember
        MARK_AS_NOT_REMEMBER -> R.string.key_gesture_action_mark_as_not_remember
        MARK_CARD_AS_LEARNED -> R.string.key_gesture_action_mark_card_as_learned
        SPEAK_QUESTION -> R.string.key_gesture_action_speak_question
        SPEAK_ANSWER -> R.string.key_gesture_action_speak_answer
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.run {
            activeRemappingKeyGesture = getSerializable(STATE_KEY_ACTIVE_REMAPPING_KEY_GESTURE)
                    as? KeyGesture
            if (activeRemappingKeyGesture != null)
                updateAdapterItems()
        }
    }

    override fun onResume() {
        super.onResume()
        showActionBar()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(STATE_KEY_ACTIVE_REMAPPING_KEY_GESTURE, activeRemappingKeyGesture)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (needToCloseDiScope()) {
            WalkingModeSettingsDiScope.close()
        }
    }

    data class KeyGestureActionItem(
        val keyGestureAction: KeyGestureAction,
        override val text: String,
        override val isSelected: Boolean
    ) : Item

    companion object {
        const val STATE_KEY_ACTIVE_REMAPPING_KEY_GESTURE = "activeRemappingKeyGesture"
    }
}
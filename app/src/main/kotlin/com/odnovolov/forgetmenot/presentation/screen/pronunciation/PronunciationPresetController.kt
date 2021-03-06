package com.odnovolov.forgetmenot.presentation.screen.pronunciation

import com.odnovolov.forgetmenot.domain.entity.Deck
import com.odnovolov.forgetmenot.domain.entity.GlobalState
import com.odnovolov.forgetmenot.domain.entity.NameCheckResult
import com.odnovolov.forgetmenot.domain.entity.checkPronunciationName
import com.odnovolov.forgetmenot.domain.interactor.decksettings.DeckSettings
import com.odnovolov.forgetmenot.domain.interactor.decksettings.PronunciationSettings
import com.odnovolov.forgetmenot.presentation.common.LongTermStateSaver
import com.odnovolov.forgetmenot.presentation.common.Navigator
import com.odnovolov.forgetmenot.presentation.common.ShortTermStateProvider
import com.odnovolov.forgetmenot.presentation.common.customview.preset.DialogPurpose.*
import com.odnovolov.forgetmenot.presentation.common.customview.preset.PresetDialogState
import com.odnovolov.forgetmenot.presentation.common.customview.preset.SkeletalPresetController
import com.odnovolov.forgetmenot.presentation.common.customview.preset.SkeletalPresetController.Command.ShowRemovePresetDialog
import com.odnovolov.forgetmenot.presentation.screen.help.HelpArticle
import com.odnovolov.forgetmenot.presentation.screen.help.HelpDiScope

class PronunciationPresetController(
    private val deckSettingsState: DeckSettings.State,
    private val pronunciationSettings: PronunciationSettings,
    private val presetDialogState: PresetDialogState,
    private val globalState: GlobalState,
    private val navigator: Navigator,
    longTermStateSaver: LongTermStateSaver,
    presetDialogStateProvider: ShortTermStateProvider<PresetDialogState>
) : SkeletalPresetController(
    presetDialogState,
    presetDialogStateProvider,
    longTermStateSaver
) {
    override fun onSetPresetButtonClicked(id: Long?) {
        pronunciationSettings.setPronunciation(pronunciationId = id!!)
    }

    override fun getPresetName(id: Long): String {
        return globalState.sharedPronunciations.first { it.id == id }.name
    }

    override fun onDeletePresetButtonClicked(id: Long) {
        val isPresetInUse: Boolean = globalState.decks
            .any { deck: Deck -> deck.exercisePreference.pronunciation.id == id }
        if (isPresetInUse) {
            presetDialogState.idToDelete = id
            sendCommand(ShowRemovePresetDialog)
        } else {
            pronunciationSettings.deleteSharedPronunciation(pronunciationId = id)
        }
    }

    override fun onPresetNamePositiveDialogButtonClicked() {
        val newPresetName: String = presetDialogState.typedPresetName
        if (checkPronunciationName(newPresetName, globalState) != NameCheckResult.Ok) return
        when (val purpose = presetDialogState.purpose) {
            ToMakeIndividualPresetAsShared -> {
                val pronunciation = deckSettingsState.deck.exercisePreference.pronunciation
                pronunciationSettings.renamePronunciation(pronunciation, newPresetName)
            }
            ToCreateNewSharedPreset -> {
                pronunciationSettings.createNewSharedPronunciation(newPresetName)
            }
            is ToRenameSharedPreset -> {
                val pronunciation = globalState.sharedPronunciations.first { it.id == purpose.id }
                pronunciationSettings.renamePronunciation(pronunciation, newPresetName)
            }
        }
    }

    override fun onRemovePresetPositiveDialogButtonClicked() {
        presetDialogState.idToDelete?.let { id: Long ->
            pronunciationSettings.deleteSharedPronunciation(pronunciationId = id)
        }
        presetDialogState.idToDelete = null
    }

    override fun onHelpButtonClicked() {
        navigator.navigateToHelpFromPronunciation {
            HelpDiScope(HelpArticle.Presets)
        }
    }
}
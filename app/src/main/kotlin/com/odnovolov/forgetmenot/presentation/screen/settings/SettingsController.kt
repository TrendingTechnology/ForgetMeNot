package com.odnovolov.forgetmenot.presentation.screen.settings

import com.odnovolov.forgetmenot.presentation.common.LongTermStateSaver
import com.odnovolov.forgetmenot.presentation.common.Navigator
import com.odnovolov.forgetmenot.presentation.common.base.BaseController
import com.odnovolov.forgetmenot.presentation.common.entity.FullscreenPreference
import com.odnovolov.forgetmenot.presentation.screen.help.HelpArticle
import com.odnovolov.forgetmenot.presentation.screen.help.HelpDiScope
import com.odnovolov.forgetmenot.presentation.screen.settings.SettingsEvent.*

class SettingsController(
    private val navigator: Navigator,
    private val fullscreenPreference: FullscreenPreference,
    private val longTermStateSaver: LongTermStateSaver
) : BaseController<SettingsEvent, Nothing>() {
    override fun handle(event: SettingsEvent) {
        when (event) {
            WalkingModeSettingsButton -> {
                navigator.navigateToWalkingModeSettingsFromSettings()
            }

            WalkingModeHelpButton -> {
                navigator.navigateToHelpFromSettings {
                    HelpDiScope(HelpArticle.WalkingMode)
                }
            }

            FullscreenInHomeAndSettingsCheckboxClicked -> {
                with(fullscreenPreference) {
                    isEnabledInHomeAndSettings = !isEnabledInHomeAndSettings
                }
            }

            FullscreenInExerciseCheckboxClicked -> {
                with(fullscreenPreference) {
                    isEnabledInExercise = !isEnabledInExercise
                }
            }

            FullscreenInRepetitionCheckboxClicked -> {
                with(fullscreenPreference) {
                    isEnabledInRepetition = !isEnabledInRepetition
                }
            }
        }
    }

    override fun saveState() {
        longTermStateSaver.saveStateByRegistry()
    }
}
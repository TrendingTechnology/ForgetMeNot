package com.odnovolov.forgetmenot.presentation.screen.intervals

import com.odnovolov.forgetmenot.domain.interactor.decksettings.IntervalsSettings
import com.odnovolov.forgetmenot.persistence.usersessionterm.IntervalsScreenStateProvider
import com.odnovolov.forgetmenot.presentation.screen.decksettings.DECK_SETTINGS_SCOPED_ID
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val intervalsModule = module {
    scope<IntervalsViewModel> {
        scoped {
            IntervalsSettings(
                deckSettings = getScope(DECK_SETTINGS_SCOPED_ID).get(),
                globalState = get()
            )
        }
        scoped { IntervalsScreenStateProvider() }
        scoped { get<IntervalsScreenStateProvider>().load() }
        scoped {
            IntervalsController(
                deckSettingsState = getScope(DECK_SETTINGS_SCOPED_ID).get(),
                intervalsSettings = get(),
                intervalsScreenState = get(),
                globalState = get(),
                longTermStateSaver = get(),
                intervalsScreenStateProvider = get<IntervalsScreenStateProvider>()
            )
        }
        viewModel {
            IntervalsViewModel(
                deckSettingsState = getScope(DECK_SETTINGS_SCOPED_ID).get(),
                intervalsScreenState = get(),
                globalState = get()
            )
        }
    }
}

const val INTERVALS_SCOPE_ID = "INTERVALS_SCOPE_ID"
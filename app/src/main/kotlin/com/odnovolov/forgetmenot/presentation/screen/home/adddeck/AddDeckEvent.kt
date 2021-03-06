package com.odnovolov.forgetmenot.presentation.screen.home.adddeck

import java.io.InputStream

sealed class AddDeckEvent {
    object HelpLoadFromFileButtonClicked : AddDeckEvent()
    object CreateDeckButtonClicked : AddDeckEvent()
    class ContentReceived(val inputStream: InputStream, val fileName: String?) : AddDeckEvent()
    class DialogTextChanged(val dialogText: String) : AddDeckEvent()
    object PositiveDialogButtonClicked : AddDeckEvent()
    object NegativeDialogButtonClicked : AddDeckEvent()
}
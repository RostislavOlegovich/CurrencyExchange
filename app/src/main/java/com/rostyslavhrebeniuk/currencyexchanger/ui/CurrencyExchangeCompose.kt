package com.rostyslavhrebeniuk.currencyexchanger.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rostyslavhrebeniuk.currencyexchanger.R
import com.rostyslavhrebeniuk.currencyexchanger.main.MainScreen
import com.rostyslavhrebeniuk.currencyexchanger.ui.dropdown.Dropdown
import com.rostyslavhrebeniuk.currencyexchanger.ui.theme.CurrencyExchangerTheme
import com.rostyslavhrebeniuk.currencyexchanger.utils.isDoubleOrDigit

@Composable
fun CurrencyExchangeCompose(
    @DrawableRes iconId: Int,
    iconBackgroundColor: Color,
    @StringRes titleId: Int,
    value: Double? = null,
    readOnly: Boolean = false,
    currencies: List<String>,
    selectedCurrency: (String) -> Unit,
    onCurrencyValueChange: (String) -> Unit = {}
) {

    var text by remember(value) {
        mutableStateOf(value?.toString() ?: "")
    }

    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FilledIconButton(
            onClick = {},
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = iconBackgroundColor,
                contentColor = Color.White,
                disabledContainerColor = iconBackgroundColor,
                disabledContentColor = Color.White,
            )
        ) {
            Icon(
                painter = painterResource(id = iconId),
                contentDescription = ""
            )
        }

        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = stringResource(id = titleId)
        )

        TextField(
            modifier = Modifier.weight(2f),
            value = text,
            textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.End),
            readOnly = readOnly,
            colors = textFieldDefaults(),
            onValueChange = {
                if (it.isDoubleOrDigit()) {
                    onCurrencyValueChange(it)
                    text = it
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Dropdown(currencies, selectedCurrency)
    }
}

@Composable
fun textFieldDefaults() = TextFieldDefaults.colors(
    cursorColor = Color.Black,
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    disabledContainerColor = Color.Transparent,
    disabledIndicatorColor = Color.Transparent,
    disabledTextColor = Color.Black
)


@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    CurrencyExchangerTheme {
        MainScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun CurrencyExchangeComposePreview() {
    CurrencyExchangeCompose(
        iconId = R.drawable.arrow_down,
        iconBackgroundColor = Color.Green,
        titleId = R.string.submit,
        selectedCurrency = {},
        currencies = listOf()
    )
}
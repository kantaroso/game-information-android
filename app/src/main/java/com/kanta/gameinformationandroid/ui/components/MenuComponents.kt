package com.kanta.gameinformationandroid.ui.components // あなたのパッケージ名

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource // 追加
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.kanta.gameinformationandroid.AppDestinations.MAKER_LIST_SCREEN_ROUTE
import com.kanta.gameinformationandroid.R // 追加 (プロジェクトにRクラスが存在する場合)
import com.kanta.gameinformationandroid.ui.theme.GameInformationAndroidTheme

/**
 * ドロップダウンメニューの各アイテムを表すデータクラス。
 *
 * @param id 各アイテムを区別するための一意なID（オプション、keyとして利用する場合など）。
 * @param title メニューに表示されるテキスト。
 * @param icon メニューアイテムの左側に表示されるアイコン（オプション）。
 * @param onItemSelected このメニューアイテムが選択されたときに実行されるアクション。
 */
data class AppMenuItem(
    val id: String? = null,
    val title: String,
    val icon: ImageVector? = null,
    val onItemSelected: () -> Unit
)

/**
 * アプリのデフォルトメニュー項目を返します。
 * @param navController 画面遷移に使用する NavController。
 */
@Composable
fun getDefaultAppMenuItems(navController: NavController): List<AppMenuItem> {
    return listOf(
        AppMenuItem(
            title = stringResource(R.string.page_title_maker_list), // stringResource を使用
            icon = Icons.AutoMirrored.Filled.List,
            onItemSelected = {
                println("MakerList selected")
                navController.navigate(MAKER_LIST_SCREEN_ROUTE)
            }
        )
    )
}


/**
 * 再利用可能なドロップダウンメニューコンポーネント。
 * IconButtonと、それに紐づくドロップダウンメニューを表示します。
 *
 * @param menuItems 表示するメニュー項目のリスト。
 * @param expandedState ドロップダウンメニューの開閉状態を外部から制御するための MutableState。
 *                      null の場合は内部で状態を管理します。
 * @param onDismissRequest メニューが閉じられるべきときに呼び出される。expandedState が null でない場合に必要。
 * @param modifier このコンポーネントに適用するModifier。
 * @param menuIcon ドロップダウンメニューを開くためのトリガーとなるアイコン。
 * @param contentDescriptionForMenuIcon メニューアイコンのコンテンツ説明。
 */
@Composable
fun AppDropdownMenu(
    menuItems: List<AppMenuItem>,
    expandedState: MutableState<Boolean>? = null,
    onDismissRequest: (() -> Unit)? = null,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    menuIcon: ImageVector = Icons.Filled.MoreVert,
    contentDescriptionForMenuIcon: String = "Open menu"
) {
    val internalExpanded = remember { mutableStateOf(false) }
    val currentExpanded = expandedState ?: internalExpanded
    val currentOnDismissRequest = onDismissRequest ?: { internalExpanded.value = false }

    Box(modifier = modifier.wrapContentSize(Alignment.TopStart)) {
        IconButton(onClick = { currentExpanded.value = true }) {
            Icon(
                imageVector = menuIcon,
                contentDescription = contentDescriptionForMenuIcon
            )
        }
        DropdownMenu(
            expanded = currentExpanded.value,
            onDismissRequest = { currentOnDismissRequest() }
        ) {
            menuItems.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item.title) },
                    onClick = {
                        item.onItemSelected()
                        currentExpanded.value = false
                    },
                    leadingIcon = item.icon?.let {
                        { Icon(imageVector = it, contentDescription = item.title) }
                    }
                )
            }
        }
    }
}


// --- プレビュー用のコード ---
@Preview(
    showBackground = true,
    name = "Dropdown Menu Opened (Preview Items)",
    widthDp = 250,
    heightDp = 300 // メニューが開いた状態が見えるように高さを調整
)
@Composable
fun AppDropdownMenuPreview() {
    GameInformationAndroidTheme {
        val navController = rememberNavController()
        val expandedState = remember { mutableStateOf(true) }
        AppDropdownMenu(
            menuItems = getDefaultAppMenuItems(navController),
            expandedState = expandedState,
            onDismissRequest = { expandedState.value = false }
        )
    }
}

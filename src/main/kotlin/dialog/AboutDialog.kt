package dialog

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import components.LinkText
import player.isWindows
import state.getResourcesFile

/**
 * 关于 对话框
 */
@Composable
fun AboutDialog(
    version: String,
    close: () -> Unit
) {
    Dialog(
        title = "关于",
        icon = painterResource("logo/logo.png"),
        onCloseRequest = { close() },
        resizable = false,
        state = rememberDialogState(
            position = WindowPosition(Alignment.Center),
            size = DpSize(645.dp, 650.dp)
        ),
    ) {
        Surface(
            elevation = 5.dp,
            shape = RectangleShape,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Divider()
                var state by remember { mutableStateOf(0) }
                LocalUriHandler.current
                if (MaterialTheme.colors.isLight) Color.Blue else Color(41, 98, 255)
                TabRow(
                    selectedTabIndex = state,
                    backgroundColor = Color.Transparent
                ) {
                    Tab(
                        text = { Text("关于") },
                        selected = state == 0,
                        onClick = { state = 0 }
                    )
                    Tab(
                        text = { Text("许可") },
                        selected = state == 1,
                        onClick = { state = 1 }
                    )
                    Tab(
                        text = { Text("致谢") },
                        selected = state == 2,
                        onClick = { state = 2 }
                    )
                    Tab(
                        text = { Text("第三方软件") },
                        selected = state == 3,
                        onClick = { state = 3 }
                    )
                }
                when (state) {
                    0 -> {
                        Column (modifier = Modifier.width(IntrinsicSize.Max).padding(start = 38.dp,top = 20.dp,end = 38.dp)){

                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                            ) {
                                Image(
                                    painter = painterResource("logo/logo.png"),
                                    contentDescription = "logo",
                                    modifier = Modifier.width(70.dp)
                                )
                            }

                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                            ) {
                                SelectionContainer {
                                    Text("Typing Leaner $version 64-bit")
                                }
                            }

                            Row(
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth().padding(top = 12.dp)
                            ) {
                                Text("如果你有任何问题或建议可以到 GitHub 提 Issue,如果没有 GitHub 账号，可以发邮件。")
                            }
                            Row{
                                Text("GitHub 地址：")
                                LinkText(
                                    text = "https://github.com/tangshimin/typing-learner",
                                    url =  "https://github.com/tangshimin/typing-learner"
                                )

                            }
                            Row{
                                Text("邮箱：            ")
                                LinkText(
                                    text = "typinglearner@outlook.com",
                                    url = "mailto:typinglearner@outlook.com"
                                )
                            }
                        }
                    }
                    1 -> {
                        val file = getResourcesFile("LICENSE")
                        if (file.exists()) {
                            val license = file.readText()
                            Box(Modifier.fillMaxWidth().height(550.dp)) {
                                val stateVertical = rememberScrollState(0)
                                Box(Modifier.verticalScroll(stateVertical)) {
                                    SelectionContainer {
                                        Text(
                                            license,
                                            modifier = Modifier.padding(start = 38.dp, top = 20.dp, end = 38.dp)
                                        )
                                    }
                                }
                                VerticalScrollbar(
                                    style = LocalScrollbarStyle.current.copy(shape = if(isWindows()) RectangleShape else RoundedCornerShape(4.dp)),
                                    modifier = Modifier.align(Alignment.CenterEnd)
                                        .fillMaxHeight(),
                                    adapter = rememberScrollbarAdapter(stateVertical)
                                )
                            }
                        }
                    }
                    2 -> {

                        Column (Modifier.padding(start = 38.dp,top = 20.dp,end = 38.dp,bottom = 20.dp)){
                            Row{
                                Text("本项目的记忆单词功能来源于  ")
                                LinkText(
                                    text = "qwerty-learner",
                                    url = "https://github.com/Kaiyiwing/qwerty-learner"
                                )
                            }
                            Row{
                                Text("感谢 qwerty-learner 的所有贡献者，让我有机会把我曾经放弃的一个 app，又找到新的方式实现。")
                            }
                            Row{
                                Text("感谢 ")
                                LinkText(
                                    text = "skywind3000",
                                    url = "https://github.com/skywind3000"
                                )
                                Text("开源")
                                LinkText(
                                    text = "ECDICT",
                                    url = "https://github.com/skywind3000/ECDICT"
                                )
                            }
                            Row{
                                LinkText(
                                    text = "libregd",
                                    url = "https://github.com/libregd"
                                )
                                Text(" 为本项目设计 Logo。")
                            }
                        }



                    }
                    3 -> {
                        Column (Modifier.padding(start = 38.dp,top = 20.dp,end = 38.dp,bottom = 20.dp)){

                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ){
                                Text("软件")
                                Text("License")
                            }
                            val GPL2 = Pair( "GPL 2","https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html")
                            val GPL3 = Pair( "GPL 3","https://www.gnu.org/licenses/gpl-3.0.en.html")
                            val Apache2 = Pair( "Apache-2.0","https://www.apache.org/licenses/LICENSE-2.0")
                            val MIT = Pair( "MIT","https://opensource.org/licenses/mit-license.php")

                            Divider()
                            Dependency(
                                name = "VLC Media Player",
                                url = "https://www.videolan.org/",
                                version = "3.17.4",
                                license = GPL2,
                            )
                            Dependency(
                                name = "VLCJ",
                                url = "https://github.com/caprica/vlcj",
                                version = "4.7.1",
                                license = GPL3,
                            )
                            Dependency(
                                name = "FlatLaf",
                                url = "https://github.com/JFormDesigner/FlatLaf",
                                version = "2.3",
                                license = Apache2,
                            )

                            Row(horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth().padding(bottom = 5.dp)){
                                Row{
                                    LinkText(
                                        text = "h2database",
                                        url = "https://www.h2database.com/html/main.html"
                                    )
                                    Spacer(Modifier.width(5.dp))
                                    Text("2.1.212")
                                }
                                Row{
                                    LinkText(
                                        text = "MPL 2.0",
                                        url = "https://www.mozilla.org/en-US/MPL/2.0/"
                                    )
                                    Text("/")
                                    LinkText(
                                        text = "EPL 1.0",
                                        url = "https://opensource.org/licenses/eclipse-1.0.php"
                                    )
                                }

                            }

                            Dependency(
                                name = "OkHttp",
                                url = "https://github.com/square/okhttp",
                                version = "4.10.0",
                                license = Apache2,
                            )
                            Dependency(
                                name = "Apache OpenNLP",
                                url = "https://opennlp.apache.org/",
                                version = "1.9.4",
                                license = Apache2,
                            )
                            Dependency(
                                name = "Apache PDFBox",
                                url = "https://pdfbox.apache.org/",
                                version = "2.0.24",
                                license = Apache2,
                            )

                            Dependency(
                                name = "Compose Desktop",
                                url = "https://github.com/JetBrains/compose-jb",
                                version = "1.1.1",
                                license = Apache2,
                            )

                            Dependency(
                                name = "jetbrains compose material3",
                                url = "https://mvnrepository.com/artifact/org.jetbrains.compose.material3/material3",
                                version = "1.0.1",
                                license = Apache2,
                            )

                            Dependency(
                                name = "material-icons-extended",
                                url = "https://mvnrepository.com/artifact/org.jetbrains.compose.material/material-icons-extended",
                                version = "1.0.1",
                                license = Apache2,
                            )

                            Dependency(
                                name = "kotlin",
                                url = "https://github.com/JetBrains/kotlin",
                                version = "1.6.10",
                                license = Apache2,
                            )

                            Dependency(
                                name = "kotlinx-coroutines-core",
                                url = "https://github.com/Kotlin/kotlinx.coroutines",
                                version = "1.6.0",
                                license = Apache2,
                            )
                            Dependency(
                                name = "kotlinx-serialization-json",
                                url = "https://github.com/Kotlin/kotlinx.serialization",
                                version = "1.3.2",
                                license = Apache2,
                            )
                            Dependency(
                                name = "maven-artifact",
                                url = "https://maven.apache.org/",
                                version = "3.8.6",
                                license = Apache2,
                            )
                            Dependency(
                                name = "subtitleConvert",
                                url = "https://github.com/JDaren/subtitleConverter",
                                version = "1.0.2",
                                license = MIT,
                            )
                            Dependency(
                                name = "LyricConverter",
                                url = "https://github.com/IntelleBitnify/LyricConverter",
                                version = "1.0.",
                                license = MIT,
                            )
                            Row(horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth().padding(bottom = 5.dp)){
                                Row{
                                    LinkText(
                                        text = "EBMLReader",
                                        url = "https://github.com/matthewn4444/EBMLReader"
                                    )
                                    Spacer(Modifier.width(5.dp))
                                    Text("0.1.0")
                                }
                            }

                            Divider()
                            Row(horizontalArrangement = Arrangement.Start,
                                modifier = Modifier.fillMaxWidth().padding(bottom = 5.dp)){

                                Text("本地词典：")
                                LinkText(
                                    text = "ECDICT 本地词典",
                                    url = "https://github.com/skywind3000/ECDICT"
                                )
                            }
                            Row(horizontalArrangement = Arrangement.Start,
                                modifier = Modifier.fillMaxWidth().padding(bottom = 5.dp)){
                                Text("单词发音：单词的语音数据来源于 ")
                                LinkText(
                                    text = "有道词典",
                                    url = "https://www.youdao.com/"
                                )
                                Text(" 在线发音 API")
                            }
                            Row(horizontalArrangement = Arrangement.Start,
                                modifier = Modifier.fillMaxWidth().padding(bottom = 5.dp)){
                                Text("本程序使用的音效：")
                                LinkText(
                                    text = "Success!!",
                                    url = "https://freesound.org/people/jobro/sounds/60445/"
                                )
                                Text("|", modifier = Modifier.padding(start = 5.dp,end =5.dp))
                                LinkText(
                                    text = "short beep",
                                    url = "https://freesound.org/people/LittleJohn13/sounds/566677/"
                                )
                                Text("|", modifier = Modifier.padding(start = 5.dp,end =5.dp))
                                LinkText(
                                    text = "hint",
                                    url = "https://freesound.org/people/dland/sounds/320181/"
                                )
                            }
                        }
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedButton(onClick = { close() }) {
                        Text("确定")
                    }
                }
            }

        }
    }
}

@Composable
fun Dependency(
    name:String,
    url:String,
    version:String,
    license:Pair<String,String>,
){
    Row(horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth().padding(bottom = 5.dp)){
        Row{
            LinkText(
                text = name,
                url = url
            )
            Spacer(Modifier.width(5.dp))
            Text(version)
        }
        LinkText(
            text = license.first,
            url = license.second
        )
    }
}
package gaur.himanshu.imagesharingapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.MutableWindowInsets
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import gaur.himanshu.imagesharingapp.ui.theme.ImageSharingAppTheme
import java.io.File

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalLayoutApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ImageSharingAppTheme {
                val currentWindowInsets = WindowInsets.systemBars
                val safeInsets = remember { MutableWindowInsets(currentWindowInsets) }
                Box(
                    modifier = Modifier
                        .padding(safeInsets.insets.asPaddingValues())
                        .background(color = colorResource(id = R.color.teal_200))
                        .fillMaxSize()
                ) {
                    ImageCaptureApp()
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ImageCaptureApp(modifier: Modifier = Modifier) {

    val permissionLaucnher = rememberMultiplePermissionsState(
        permissions = listOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    )
    LaunchedEffect(Unit) {
        permissionLaucnher.launchMultiplePermissionRequest()
    }
    val captureImageLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.TakePicture()) {


        }

    val imageUri = remember {
        mutableStateOf<Uri?>(null)
    }
    val imageFile = remember {
        mutableStateOf<File?>(null)
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        val context = LocalContext.current

        Button(onClick = {
            val fileName = "capture_${System.currentTimeMillis()}"
            val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val file = File.createTempFile(fileName, ".jpg", storageDir)
            imageFile.value = file
            val fileUri =
                FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            imageUri.value = fileUri
            captureImageLauncher.launch(fileUri)
        }) {
            Text(text = "Capture")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = {
            val fileUri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                imageFile.value!!
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/*"
                putExtra(Intent.EXTRA_STREAM, fileUri)
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            context.startActivity(Intent.createChooser(intent, "Choose app to share this file: "))
        }) {
            Text(text = "Share File")
        }

    }

}


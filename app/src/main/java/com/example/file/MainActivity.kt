package com.example.file

import FileAdapter
import FolderAdapter
import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File
import java.io.IOException

class MainActivity : ComponentActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var folderAdapter: FolderAdapter
    private lateinit var fileAdapter: FileAdapter

    private var currentFolder: File? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        requestExternalStoragePermission()

        // Add permission to access external storage
        requestPermissions(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), 1
        )

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        folderAdapter = FolderAdapter()
        fileAdapter = FileAdapter()
        recyclerView.adapter = folderAdapter

        // Get the root directory of external storage
        val rootDirectory = File(Environment.getExternalStorageDirectory().absolutePath)

        // Check if external storage is available
        if (rootDirectory.exists() && rootDirectory.isDirectory) {
            // Display the contents of the external storage root directory
            displayFilesInFolder(rootDirectory)

            // Set item click listener for folders
            folderAdapter.setOnItemClickListener { folder ->
                currentFolder = folder
                displayFilesInFolder(folder)
            }
        } else {
            Log.e("TAG", "External storage is not available.")
        }

        // Get the button reference and set click listener
        val buttonCreateFile: Button = findViewById(R.id.buttonCreateFile)
        buttonCreateFile.setOnClickListener {
            createFileOnSDCard()
        }

        // Add a back button click listener
        val buttonBack: Button = findViewById(R.id.buttonBack)
        buttonBack.setOnClickListener {
            goBackToParentFolder()
        }
    }

    private fun displayFilesInFolder(folder: File) {
        // List all files in the clicked folder
        val files = folder.listFiles()
        files?.let {
            if (files.isEmpty()) {
                Log.d("TAG", "Folder is empty.")
            }

            if (folder != Environment.getExternalStorageDirectory()) {
                // Add a fake item at position 0 to represent the "Go Back" option
                folderAdapter.setFolders(listOf(File("..")) + files.asList())
            } else {
                folderAdapter.setFolders(files.asList())
            }

            switchToFoldersView()
        }
    }

    private fun goBackToParentFolder() {
        currentFolder?.let { folder ->
            val parentFolder = folder.parentFile
            parentFolder?.let {
                currentFolder = parentFolder
                displayFilesInFolder(parentFolder)
            }
        }
    }

    private fun switchToFilesView() {
        recyclerView.adapter = fileAdapter
    }

    private fun switchToFoldersView() {
        recyclerView.adapter = folderAdapter
    }

    private fun createFileOnSDCard() {
        val fileName = "example.txt"

        try {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
                put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
            }

            val resolver = contentResolver
            val uri =
                resolver.insert(MediaStore.Files.getContentUri("external"), contentValues)

            uri?.let { documentUri ->
                resolver.openOutputStream(documentUri)?.use { outputStream ->
                    // Write your file content to the outputStream
                    outputStream.write("Hello, this is the content of the file.".toByteArray())
                    Log.d("TAG", "File created: $documentUri")
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("TAG", "Error creating file: ${e.message}")
        }
    }

    private fun requestExternalStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val permission = Manifest.permission.WRITE_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this, arrayOf(permission), PERMISSION_REQUEST_CODE)
            }
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1
    }
}

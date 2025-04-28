package com.eposter.bookshelfapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.eposter.bookshelfapp.data.Book
import com.eposter.bookshelfapp.network.BookApi
import com.eposter.bookshelfapp.ui.theme.BookshelfAppTheme
import com.eposter.bookshelfapp.viewmodel.BookViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Test API directly
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("MainActivity", "Testing API directly")
                val response = BookApi.service.searchBooks("android")
                Log.d("MainActivity", "API test result: ${response.totalItems} items found")
                if (response.items != null) {
                    Log.d("MainActivity", "First book: ${response.items[0].volumeInfo.title}")
                } else {
                    Log.d("MainActivity", "No items in response")
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "API test failed", e)
            }
        }
        
        enableEdgeToEdge()
        setContent {
            BookshelfAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    val viewModel: BookViewModel = viewModel(
                        factory = ViewModelProvider.AndroidViewModelFactory(application)
                    )
                    BookshelfApp(viewModel)
                }
            }
        }
    }
}

@Composable
fun BookshelfApp(viewModel: BookViewModel) {
    Column() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary) // Using primary color from theme which will match system bars
        ) {
            Text(
                text = "Bookshelf",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onPrimary // Text color that contrasts with the background
            )
        }
        
        // Add search bar
        SearchBar(
            onSearch = { query ->
                viewModel.loadBooks(query)
            }
        )
        
        // Show offline indicator if in offline mode
        if (viewModel.isOfflineMode) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Offline",
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Offline mode - showing cached results",
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }
        
        if (viewModel.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (viewModel.errorMessage != null && !viewModel.isOfflineMode) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = viewModel.errorMessage ?: "Unknown error",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            BookshelfGrid(books = viewModel.books)
        }
    }
}

@Composable
fun SearchBar(onSearch: (String) -> Unit) {
    var searchText by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    
    OutlinedTextField(
        value = searchText,
        onValueChange = { searchText = it },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        placeholder = { Text("Search for books...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                if (searchText.isNotBlank()) {
                    onSearch(searchText)
                    focusManager.clearFocus()
                }
            }
        )
    )
}

@Composable
fun BookTile(book: Book) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .aspectRatio(0.7f)
    ) {
        Column {
            // Book cover
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                if (book.thumbnailUrl != null) {
                    var isLoading by remember { mutableStateOf(true) }
                    var isError by remember { mutableStateOf(false) }
                    
                    if (isLoading && !isError) {
                        CircularProgressIndicator()
                    }
                    
                    AsyncImage(
                        model = book.thumbnailUrl,
                        contentDescription = "Cover of ${book.title}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize(),
                        onLoading = { isLoading = true },
                        onSuccess = { isLoading = false },
                        onError = { 
                            isLoading = false
                            isError = true
                            Log.e("BookTile", "Error loading image: ${book.thumbnailUrl}")
                        }
                    )
                    
                    if (isError) {
                        Text(
                            text = "📚",
                            style = MaterialTheme.typography.displayMedium
                        )
                    }
                } else {
                    Text(
                        text = "📚",
                        style = MaterialTheme.typography.displayMedium
                    )
                }
            }
            
            // Book details
            Column(
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = book.author,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun BookshelfGrid(books: List<Book>) {
    // Use rememberLazyGridState to better manage the grid state
    val gridState = rememberLazyGridState()
    
    LazyVerticalGrid(
        columns = GridCells.Adaptive(128.dp),
        state = gridState,
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            count = books.size,
            key = { index -> books[index].id } // Using a stable key helps with recomposition
        ) { index ->
            BookTile(book = books[index])
        }
    }
}

// Keep the preview functions but update them to use the new models
//@Preview(showBackground = true)
//@Composable
//fun BookshelfAppPreview() {
//    BookshelfAppTheme {
//        BookshelfApp()
//    }
//}

@Preview(showBackground = true)
@Composable
fun BookTilePreview() {
    BookshelfAppTheme {
        BookTile(
            Book(
                id = "1",
                title = "Sample Book",
                author = "Sample Author",
                thumbnailUrl = null,
                description = "Sample description"
            )
        )
    }
}

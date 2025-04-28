package com.eposter.bookshelfapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.eposter.bookshelfapp.data.Book
import com.eposter.bookshelfapp.ui.theme.BookshelfAppTheme
import com.eposter.bookshelfapp.viewmodel.BookViewModel

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BookshelfAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    BookshelfApp()
                }
            }
        }
    }
}

@Composable
fun BookshelfApp(viewModel: BookViewModel = viewModel()) {
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
        
        if (viewModel.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (viewModel.errorMessage != null) {
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
                    .fillMaxWidth()
                    .background(Color(0xFF90CAF9)),
                contentAlignment = Alignment.Center
            ) {
                if (book.thumbnailUrl != null) {
                    AsyncImage(
                        model = book.thumbnailUrl,
                        contentDescription = "Cover of ${book.title}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
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
        columns = GridCells.Fixed(2),
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
@Preview(showBackground = true)
@Composable
fun BookshelfAppPreview() {
    BookshelfAppTheme {
        BookshelfApp()
    }
}

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

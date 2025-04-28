package com.eposter.bookshelfapp

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
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eposter.bookshelfapp.ui.theme.BookshelfAppTheme

class MainActivity : ComponentActivity() {
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
fun BookshelfApp() {
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
        BookshelfGrid()
    }
}

@Composable
fun BookTile(title: String = "Book Title", author: String = "Author Name") {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .aspectRatio(0.7f)
    ) {
        Column {
            // Book cover placeholder
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(0xFF90CAF9)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📚",
                    style = MaterialTheme.typography.displayMedium
                )
            }
            
            // Book details
            Column(
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = author,
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
fun BookshelfGrid() {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize()
    ) {
        items(10) { index ->
            BookTile(
                title = "Book ${index + 1}",
                author = "Author ${index + 1}"
            )
        }
    }
}

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
        BookTile()
    }
}

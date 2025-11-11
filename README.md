# Beauty App - Android (Kotlin + Jetpack Compose)

This is the Kotlin/Android version of your React/TypeScript Beauty App, converted to use Jetpack Compose.

## 🎨 Features

- **Home Screen** with AR-style face overlay and featured products carousel
- **Products Grid** with 2-column layout
- **Like/Favorite** functionality with heart icons (top-left of product cards)
- **Add to Cart** functionality with + buttons (top-right of product cards)
- **Cart Screen** with quantity controls and total calculation
- **Bottom Navigation** with elevated center AI Scan button
- **API Integration** using Retrofit to fetch products from makeup-api.herokuapp.com
- **State Management** using ViewModel and StateFlow
- **Image Loading** with Coil library

## 📁 Project Structure

```
app/src/main/java/com/example/beautyapp/
├── MainActivity.kt                      # Main entry point
├── data/
│   └── Product.kt                       # Product data model
├── network/
│   └── MakeupApiService.kt             # Retrofit API service
├── viewmodel/
│   └── MainViewModel.kt                # ViewModel for state management
├── ui/
│   ├── components/
│   │   ├── ProductCard.kt              # Product card with heart + plus buttons
│   │   └── BottomNavBar.kt             # Bottom navigation bar
│   ├── screens/
│   │   ├── HomeScreen.kt               # Home/Face scan screen
│   │   ├── ProductsScreen.kt           # Products grid screen
│   │   └── CartScreen.kt               # Shopping cart screen
│   └── theme/
│       ├── Theme.kt                     # App theme
│       └── Typography.kt                # Typography definitions
```

## 🚀 Getting Started

### Prerequisites

- Android Studio (Hedgehog or later)
- JDK 8 or higher
- Android SDK (API 24+)

### Setup Instructions

1. **Open the project in Android Studio**
   - File → Open → Select the `android-beauty-app` folder

2. **Sync Gradle**
   - Android Studio should automatically sync
   - If not, click "Sync Project with Gradle Files"

3. **Run the app**
   - Connect an Android device or start an emulator
   - Click the "Run" button or press Shift+F10

## 🔑 Key Components

### Data Model
```kotlin
data class Product(
    val id: Int,
    val brand: String?,
    val name: String?,
    val price: String?,
    val imageLink: String?,
    val productType: String?,
    val description: String?
)
```

### ViewModel State
```kotlin
data class AppState(
    val products: List<Product> = emptyList(),
    val likedProducts: Set<Int> = emptySet(),
    val cartItems: Map<Int, Int> = emptyMap(),
    val loading: Boolean = false,
    val activeTab: String = "home"
)
```

### Product Card Features
- **Heart Icon (Top-Left)**: Toggle like/favorite
- **Plus Icon (Top-Right)**: Add to cart
- Both icons turn into checkmarks briefly when clicked
- Colored backgrounds (alternating: amber, emerald, slate, rose, blue, purple)

### Bottom Navigation
- Home
- Clean Products
- **AI Face Scan** (center, elevated pink circle)
- Cart (with badge showing item count)
- Profile

## 📦 Dependencies

- **Jetpack Compose** - Modern UI toolkit
- **Material3** - Material Design 3 components
- **Retrofit** - HTTP client for API calls
- **Gson** - JSON serialization
- **Coil** - Image loading library
- **Coroutines** - Asynchronous programming
- **ViewModel** - State management

## 🎯 Screens

### 1. Home Screen
- Full-screen face image with AR overlay
- "Special for you" text
- Horizontal carousel of 3 featured products
- "View all products" CTA button

### 2. Products Screen
- Header with back button, title, and profile image
- 2-column grid layout
- Each product card has:
  - Colored background
  - Product image
  - Heart icon (top-left) for favorites
  - Plus icon (top-right) to add to cart
  - Product name below card

### 3. Cart Screen
- List of items with quantity controls
- Each item shows: image, name, brand, price
- +/- buttons to adjust quantity
- Total price calculation
- Checkout button

### 4. Favorites Screen
- Same as Products Screen but filtered to show only liked items

## 🔄 State Management

The app uses `MainViewModel` with `StateFlow` for reactive state management:

- `fetchProducts()` - Load products from API
- `toggleLike(productId)` - Add/remove from favorites
- `addToCart(productId)` - Increment cart quantity
- `removeFromCart(productId)` - Decrement cart quantity
- `setActiveTab(tab)` - Change active screen

## 🎨 Styling

### Colors
- **Primary Pink**: `#F472B6` (Pink-400)
- **Secondary Pink**: `#EC4899` (Pink-500)
- **Green (Success)**: `#10B981` (Emerald-500)
- **Red (Like)**: `#EF4444` (Red-500)

### Card Background Colors
- Amber-50, Emerald-50, Slate-100, Rose-50, Blue-50, Purple-50

## 📱 API

The app fetches products from:
```
https://makeup-api.herokuapp.com/api/v1/products.json
```

## Android YouTube Player 
```
https://github.com/PierfrancescoSoffritti/android-youtube-player
```

## 🐛 Troubleshooting

### API Not Loading
- Check internet connection
- Verify `INTERNET` permission in AndroidManifest.xml
- Check if API endpoint is accessible

### Images Not Loading
- Ensure Coil dependency is properly added
- Check image URLs are valid
- Verify internet permission

### Build Errors
- Clean project: Build → Clean Project
- Rebuild: Build → Rebuild Project
- Invalidate caches: File → Invalidate Caches → Restart

## 🔮 Future Enhancements

- Search functionality
- Product filtering
- User authentication
- Persistent storage (Room database)
- Animations and transitions
- AI face scanning feature
- Payment integration

## 📄 License

This project is a conversion from React/TypeScript to Kotlin/Jetpack Compose for educational purposes.

---

**Happy Coding! 🚀**

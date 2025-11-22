package com.example.amilimetros.data.repository



import com.example.amilimetros.data.local.product.ProductDao
import com.example.amilimetros.data.local.product.ProductEntity
import kotlinx.coroutines.flow.Flow

class ProductRepository(private val productDao: ProductDao) {

    fun getAllProducts(): Flow<List<ProductEntity>> = productDao.getAllFlow()

    fun getProductsByCategory(category: String): Flow<List<ProductEntity>> =
        productDao.getByCategory(category)

    suspend fun getProductById(id: Long): ProductEntity? = productDao.getById(id)

    suspend fun addProduct(product: ProductEntity): Result<Long> {
        return try {
            val id = productDao.insert(product)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProduct(product: ProductEntity): Result<Unit> {
        return try {
            productDao.update(product)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProduct(product: ProductEntity): Result<Unit> {
        return try {
            productDao.delete(product)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
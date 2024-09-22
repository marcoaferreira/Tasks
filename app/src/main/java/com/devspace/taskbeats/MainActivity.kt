package com.devspace.taskbeats

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var categories = listOf<CategoryUiData>()
    private var tasks = listOf<TaskUiData>()
    private val categoryAdapter = CategoryListAdapter()


    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            TasksDatabase::class.java, "database-tasks"
        ).build()
    }

    private val categoryDao: CategoryDao by lazy {
        db.getCategoryDao()
    }

    private val taskDao: TaskDao by lazy {
        db.getTaskDao()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        insertDefaultCategory()
//        insertDefaultTask()

        val rvCategory = findViewById<RecyclerView>(R.id.rv_categories)
        val rvTask = findViewById<RecyclerView>(R.id.rv_tasks)

        val taskAdapter = TaskListAdapter()

        categoryAdapter.setOnClickListener { selected ->
            if(selected.name == " + ") {
                val createCategoryBottomSheet = CreateCategoryBottomSheet { categoryName ->
                    val categoryEntity = CategoryEntity (
                        name = categoryName,
                        isSelected = false
                    )
                    insertCategory(categoryEntity)
                }
                createCategoryBottomSheet.show(supportFragmentManager, "createCategoryBottomSheet")
            } else {
                val categoryTemp = categories.map { item ->
                    when {
                        item.name == selected.name && !item.isSelected -> item.copy(
                            isSelected = true
                        )

                        item.name == selected.name && item.isSelected -> item.copy(isSelected = false)
                        else -> item
                    }
                }

                val taskTemp =
                    if (selected.name != "ALL") {
                        tasks.filter { it.category == selected.name }
                    } else {
                        tasks
                    }

                taskAdapter.submitList(taskTemp)

                categoryAdapter.submitList(categoryTemp)
            }
        }

        rvCategory.adapter = categoryAdapter
        GlobalScope.launch(Dispatchers.IO) {
            getCategoriesFromDatabase()
        }


        rvTask.adapter = taskAdapter
        getTasksFromDatabase(taskAdapter)
    }

    private fun getCategoriesFromDatabase(){
            val categoriesFromDb: List<CategoryEntity> = categoryDao.getAll()
            val categoriesUiData= categoriesFromDb.map{
                CategoryUiData(
                    name = it.name,
                    isSelected = it.isSelected
                )
            }.toMutableList()
            categoriesUiData.add(
                CategoryUiData(
                    name = " + ",
                    isSelected = false
                )
            )
            GlobalScope.launch(Dispatchers.Main) {
                categories = categoriesUiData
                categoryAdapter.submitList(categoriesUiData)
            }
    }

    private fun getTasksFromDatabase(adapter: TaskListAdapter){
        GlobalScope.launch(Dispatchers.IO) {
            val TasksFromDb: List<TaskEntity> = taskDao.getAll()
            val tasksUiData = TasksFromDb.map{
                TaskUiData(
                    category = it.category,
                    name = it.name
                )
            }
            GlobalScope.launch(Dispatchers.Main) {
                tasks = tasksUiData
                adapter.submitList(tasksUiData)
            }
        }
    }

    private fun insertCategory (categoryEntity: CategoryEntity){
        GlobalScope.launch(Dispatchers.IO) {
            categoryDao.insert(categoryEntity)
            getCategoriesFromDatabase()
        }
    }

}
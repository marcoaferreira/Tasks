package com.devspace.taskbeats

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var categories = listOf<CategoryUiData>()
    private var tasks = listOf<TaskUiData>()

    private val categoryAdapter = CategoryListAdapter()
    private val taskAdapter by lazy {
        TaskListAdapter()
    }


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
        val fabCreateTask = findViewById<FloatingActionButton>(R.id.fab_create_task)

        fabCreateTask.setOnClickListener {
            showCreateUpdateTaskBottomSheet()
        }

        taskAdapter.setOnClickListener { task ->
            showCreateUpdateTaskBottomSheet(task)
        }

        categoryAdapter.setOnClickListener { selected ->
            if(selected.name == " + ") {
                val createCategoryBottomSheet = CreateCategoryBottomSheet { categoryName ->
                    val categoryEntity = CategoryEntity (
                        name = categoryName,
                        isSelected = false
                    )
                    insertCategory(categoryEntity)
                }
                createCategoryBottomSheet.show(
                    supportFragmentManager,
                    "createCategoryBottomSheet"
                )
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

        GlobalScope.launch(Dispatchers.IO) {
            getTasksFromDatabase()
        }
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

    private fun getTasksFromDatabase(){
            val TasksFromDb: List<TaskEntity> = taskDao.getAll()
            val tasksUiData = TasksFromDb.map{
                TaskUiData(
                    id = it.id,
                    category = it.category,
                    name = it.name
                )
            }
            GlobalScope.launch(Dispatchers.Main) {
                tasks = tasksUiData
                taskAdapter.submitList(tasksUiData)
            }
    }

    private fun insertCategory (categoryEntity: CategoryEntity){
        GlobalScope.launch(Dispatchers.IO) {
            categoryDao.insert(categoryEntity)
            getCategoriesFromDatabase()
        }
    }

    private fun insertTask(taskEntity: TaskEntity) {
        GlobalScope.launch(Dispatchers.IO) {
            taskDao.insert(taskEntity)
            getTasksFromDatabase()
        }

    }

    private fun updateTask(taskEntity: TaskEntity){
        GlobalScope.launch(Dispatchers.IO) {
            taskDao.update(taskEntity)
            getTasksFromDatabase()
        }
    }

    private fun showCreateUpdateTaskBottomSheet(taskUiData: TaskUiData? = null){
        val createTaskBottomSheet = CreateOrUpdateTaskBottomSheet(
            task = taskUiData,
            categoryList = categories,
            onCreateClicked = {
                    tasksToBeCreated ->
                val taskEntityToBeInserted = TaskEntity(
                    name = tasksToBeCreated.name,
                    category = tasksToBeCreated.category
                )
                insertTask(taskEntityToBeInserted)
            },
            onUpdateClicked = { taskToBeUpdated ->
                val taskEntityToBeUpdated = TaskEntity(
                    id = taskToBeUpdated.id,
                    name = taskToBeUpdated.name,
                    category = taskToBeUpdated.category
                )
                updateTask(taskEntityToBeUpdated)
            }
        )
        createTaskBottomSheet.show (
            supportFragmentManager,
            "createTaskBottomSheet"
        )
    }

}
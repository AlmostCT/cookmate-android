package almostct.top.foodhack.ui.comments

import activitystarter.Arg
import almostct.top.foodhack.R
import almostct.top.foodhack.model.AddCommentRequest
import almostct.top.foodhack.model.Comment
import almostct.top.foodhack.ui.common.InjectableActivity
import almostct.top.foodhack.ui.common.defaultSub
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AlertDialog
import android.support.v7.widget.CardView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.marcinmoskala.activitystarter.argExtra
import io.reactivex.Observable
import kotterknife.bindView


private val testAccountId = "5a9b7a113cfe433b705e1250"

class CommentsActivity : InjectableActivity() {

    val commentsView by bindView<RecyclerView>(R.id.comments_recycler)

    @get:Arg
    var target: String by argExtra()

    @get:Arg
    var stepId: Int by argExtra()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments)
        val toolbar = findViewById(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val fab = findViewById(R.id.fab) as FloatingActionButton
        fab.setOnClickListener { view ->
            newCommentBuilder()
        }

        fetchUI().defaultSub { updateUI(it) }
    }

    override fun getActivityTitle(): String {
        return getString(R.string.string_comments)
    }

    fun fetchUI(): Observable<List<Comment>> {
        return getClient().getAllComments(target, stepId)
    }

    fun updateUI(comments: List<Comment>) {
        commentsView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        commentsView.adapter = CommentsAdapter(comments)
    }

    private fun newCommentBuilder() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.new_comment_dialog_title))

// Set up the input
        val input = EditText(this)
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE
        builder.setView(input)

// Set up the buttons
        builder.setPositiveButton("OK",
            { dialog, which ->
                val text = input.text.toString()
                Toast.makeText(
                    this,
                    text,
                    Toast.LENGTH_SHORT
                ).show()
                getClient().postComment(AddCommentRequest(testAccountId, target, stepId, text))
                    .concatMap { fetchUI() }
                    .defaultSub { updateUI(it) }
            })
        builder.setNegativeButton("Cancel",
            { dialog, which -> dialog.cancel() })

        builder.show()
    }

    inner class CommentsAdapter(private val comments: List<Comment>) :
        RecyclerView.Adapter<CommentsAdapter.MyViewHolder>() {

        inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val textView by bindView<TextView>(R.id.comment_info_text)
            val username by bindView<TextView>(R.id.comment_item_nickname)
            val cardView by bindView<CardView>(R.id.comment_card_view)
            val likeBtn by bindView<Button>(R.id.comment_likes)
            val dislikeBtn by bindView<Button>(R.id.comment_dislikes)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.comments_item, parent, false)
            return MyViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val newItem = comments[position]
            holder.username.text = newItem.account.handle
            holder.textView.text = newItem.text
            holder.likeBtn.text = newItem.likes.toString()
            holder.dislikeBtn.text = newItem.dislikes.toString()
            holder.cardView.setOnClickListener {
                Toast.makeText(
                    this@CommentsActivity,
                    newItem.text,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        override fun getItemCount() = comments.size
    }
}

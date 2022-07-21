
class UserScoresView(context: Context, attrs: AttributeSet?) :
    ConstraintLayout(context, attrs) {

    private val minHeight: Int = 1.dpToPx
    private val scoresAdapter = ScoresAdapter()

    private val binding = UserScoresViewBinding.inflate(LayoutInflater.from(context), this).apply {
        recyclerView.adapter = scoresAdapter
        setOnClickListener {
            isExpanded = !isExpanded
        }
    }
    private var isExpanded = true
        set(value) {
            if (value == field) return
            field = value
            AnimationUtils.rotate(binding.arrowImage, if (isExpanded) arrowAngle else 0f)
            toggleViewVisibility(binding.scoresViewContainer, !isExpanded)
            toggleViewVisibility(binding.recyclerView, isExpanded)
        }

    var userActivitiesScores: List<String> = emptyList()
        set(value) {
            field = value
            binding.scoresView.stationsScores = value
            toggleViewVisibility(binding.scoresViewContainer, !isExpanded)
        }

    var userActivities = emptyList<UserActivityCircle>()
        set(value) {
            field = value
            scoresAdapter.submitList(value)
        }

    init {
        context.obtainStyledAttributes(attrs, styleable.UserScoresView).apply {
            try {
                val text = getText(styleable.UserScoresView_title_text)
                binding.headerTitle.text = text
            } finally {
                recycle()
            }
        }
    }

    private fun toggleViewVisibility(view: View, isExpanded: Boolean) {
        AnimationUtils.animateHeight(
            view = view,
            expand = isExpanded,
            minHeight = minHeight,
            maxHeight = getMaxHeight(view),
        )
    }

    private fun getMaxHeight(view: View): Int {
        view.measure(
            MeasureSpec.makeMeasureSpec(
                this.measuredWidth, MeasureSpec.EXACTLY
            ),
            MeasureSpec.UNSPECIFIED
        )
        return view.measuredHeight
    }
}

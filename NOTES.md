#SELECTION

I think that section collapse and section/item/footer selection state should probably be treated as "data source" get methods. Without setters! The default implementations should return false. If you want selection or section collapse, you should  implement it as part of your data source.

This means all my selection code is garbage! GARBAGE!

... DOES IT?


#TESTING:
	- need to ensure this works fine for adapters which don't have footer views and which don't have header views (!!!)
	- need to test against different top/bottom padding scenarios

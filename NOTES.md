#BUGS

Consider a 'first run' scenario with an empty data store. Figure out how to show the loading indicator!

#TODO: 
I need to figure out how to handle the situation where there's no more data. How to gracefully tell the scroll listener no new data is forthcoming. I should also add a third item type ("No more data!") to indicate we've reached the end of the line. 
	- Consider a parameter to the LoadCompleteNotifier::notifyLoadComplete(boolean exhausted)


#TESTING:
	- need to ensure this works fine for adapters which don't have footer views and which don't have header views (!!!)
	- need to test against different top/bottom padding scenarios

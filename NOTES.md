#BUGS
	- headers aren't animated when recycler view runs addition/removal animations
	- addressbook demo loses rows when scrolling, sometimes. wtf

#TESTING:
	- need to ensure this works fine for adapters which don't have footer views and which don't have header views (!!!)
	- need to test against different top/bottom padding scenarios


#THOUGHTS ON ALTERNATE APPROACHES

I could subclass the default linear layout manager. I'd have my adapter set a tag on headers (using ids from a values.xml in the library) that marks them as special. All items (including ghost headers) would get a tag that marks which section they belong to.

In onLayoutChildren and scrollVerticallyBy I would call my positionHeaders method which would work similarly to the current one. It would walk the children of the RV, find which sections are represented via tags, and vend if necessary the header (keeping headers in a hashmap like now) and then position them. I suppose I would allow natural recycling to happen, so I'd want to use weak references in my hashmap.



#RecyclerView.LayoutManager

Here's my understanding of how a linear layout manager works
- When RV has a change to the number of items it represents, onLayoutChildren is called, and in there, a number of views are vended and positioned (precisely)
	- note that in the demo code log call, top is a sane number like -163 representing the top of the first item. items are stacked according to height until they fall off the bottom.
	
- When user scrolls, scrollVerticallyBy is called
	- items are vertically shifted
	- new spaces are filled via same item vending process as onLayoutChildren
	- out of bounds items are recycled
	
firstPosition seems to be the position in the logical list of the first item on screen - it's used to know which item to vend from the adapter
	- firstPosition is updated on scroll and in recyling offscreen views

LayoutManager::getChildCount() returns number of views attached to RV, not number of hypothetical views in list

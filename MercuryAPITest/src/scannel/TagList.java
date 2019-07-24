package scannel;

import java.util.ArrayList;

import com.thingmagic.TagReadData;

public class TagList {

	ArrayList<TagUnit> tagList;
	
	public TagList() {
		// TODO Auto-generated constructor stub
		tagList = new ArrayList<TagUnit>();
	}

//	public void addTag(String epc){
//		if (tagList == null){
//			tagList = new ArrayList<TagUnit>();
//			TagUnit tag = new TagUnit(epc);
//			tagList.add(tag);
//		} else {
//			if (this.checkList(epc) == null){
//				TagUnit tag = new TagUnit(epc);
//				tagList.add(tag);
//			}
//		}
//	}
	
	public void addTag(String epc, int count){
		System.out.println("TagList.addTag(), epc="+epc+", count="+count);
		if (tagList == null){
			System.out.println("No tag list, so create new one and add the tag data...");
			tagList = new ArrayList<TagUnit>();
			TagUnit tag = new TagUnit(epc, count);
			tagList.add(tag);
		} else {
			TagUnit tag = this.checkList(epc);
			if (tag == null){
				System.out.println("No tag data with epc ["+epc+"] was found in the list.");
				TagUnit newTag = new TagUnit(epc, count);
				tagList.add(newTag);
			} else {
				System.out.println("Tag found! So add read count.");
				tag.addReadCount(count);
			}
		}
		System.out.println();
	}
	
	public void addTag(TagReadData trd){
		System.out.println("TagList.addTag() with trd, epc="+trd.epcString()+", count="+trd.getReadCount());
		if (tagList == null){
			System.out.println("No tag list, so create new one and add the tag data...");
			tagList = new ArrayList<TagUnit>();
			TagUnit tag = new TagUnit(trd.epcString(), trd.getReadCount());
			tagList.add(tag);
		} else {
			TagUnit tag = this.checkList(trd.epcString());
			if (tag == null){
				System.out.println("No tag data with epc ["+trd.epcString()+"] was found in the list.");
				TagUnit newTag = new TagUnit(trd.epcString(), trd.getReadCount());
				tagList.add(newTag);
			} else {
				System.out.println("Tag found! So add read count.");
				tag.addReadCount(trd.getReadCount());
			}
		}
		System.out.println();
	}
	
	public void removeTag(String epc){
		System.out.println("TagList.removeTag(), epc="+epc);
		if (tagList == null){
			return;
		} else {
			TagUnit tag = this.checkList(epc);
			tagList.remove(tag);
		}
	}
	
	public void reset(){
		if (tagList != null){
			tagList.clear();
		}
	}
	
	public int size(){
		if (tagList != null){
			return tagList.size();
		} else {
			return -1;
		}
	}
	
	public TagUnit get(int index){
		if (tagList != null){
			return tagList.get(index);
		} else {
			return null;
		}
	}
	
	private TagUnit checkList(String epc){
		if (tagList != null){
			for (int i=0; i<tagList.size(); i++){
				TagUnit tag = tagList.get(i);
				if (tag.getEPC().equals(epc)){
					return tag;
				}
			}
		}
		
		return null;
	}
	
	public void printListContent(){
		if (tagList != null){
			for (int i=0; i<tagList.size(); i++){
				TagUnit tag = tagList.get(i);
				System.out.println("epc=" + tag.getEPC() +", readCount=" + tag.getReadCount());
			}
		}
	}
}

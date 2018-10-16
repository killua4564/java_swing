import java.util.List;
import java.util.LinkedList;

class Member {

    public String firstname;
    public String lastname;
    public int age;

    public Member(String firstname, String lastname, int age) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.age = age;
    }

    public String toString() {
        return firstname + " " + lastname + " age=" + age;
    }
}

interface Filter<T> {
    boolean accept(T y);
}

public class FilterTool<T> {
	private List<T> select(T x[], Filter<T> filter) {
        List<T> list = new LinkedList();
        for (T v : x) {
            if (filter.accept(v)) {
                list.add(v);
            }
        }
        return list;
    }

    public void printList(List<T> list) {
    	for (T i : list) {
    		System.out.println(i.toString());
    	}
    }

	public static void main(String args[]) {
		Member members[] = {
            new Member("Paul", "Lee", 23),
            new Member("Alice", "Wang", 39),
            new Member("Sophia", "Chen", 34),
            new Member("Steph", "Lee", 28),
            new Member("Joyce", "Chang", 21)
        };
        
        FilterTool filterTool = new FilterTool<Member>();
        List<Member> result = filterTool.select(members, new Filter<Member>() {
        	public boolean accept(Member x) {
        		return x.age > 30;
        	}
        });
        filterTool.printList(result);
	}
}
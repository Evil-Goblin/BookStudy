package chapter09.designpattern.observer;

public class Client {
    public static void main(String[] args) {
        Feed feed = new Feed();

//        feed.registerObserver(new NYTimes());
//        feed.registerObserver(new Guardian());
//        feed.registerObserver(new LeMonde());

        feed.registerObserver(tweet -> {
            if (tweet != null && tweet.contains("money")) {
                System.out.println("Breaking news in NY! " + tweet);
            }
        });

        feed.registerObserver(tweet -> {
            if (tweet != null && tweet.contains("queen")) {
                System.out.println("Yet more news from London... " + tweet);
            }
        });

        feed.notifyObservers("The queen aid her favourite book is Modern Java in Action!");
    }
}

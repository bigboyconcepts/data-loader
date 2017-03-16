package org.skynetsoftware.dataloader;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MyClass
{
    public static void main(String[] args) throws UnsupportedEncodingException, InterruptedException
    {
        User user = new User();
        user.setFirstName("Predrag");
        user.setLastName("Cokulov");
        user.setAge(25);

        //Cache.getInstance(MemCache.class).addToCache("user:pedja", user);
        org.skynetsoftware.dataloader.Cache.getInstance(org.skynetsoftware.dataloader.FSCache.class).addToCache("user:pedja", user);

        Thread.sleep(5000);//let it write to cache

        DataLoader<User> dataLoader = new DataLoader<User>()
        {
            @Override
            protected void runOnUiThread(Runnable runnable)
            {
                runnable.run();
            }
        };

        List<DataProvider<User>> providers = new ArrayList<>();
        CacheDataProvider<MemCache, User> memCacheDataProvider = new CacheDataProvider<>("user:pedja", MemCache.class);
        CacheDataProvider<org.skynetsoftware.dataloader.FSCache, User> fsCacheDataProvider = new CacheDataProvider<>("user:pedja", FSCache.class);
        providers.add(memCacheDataProvider);
        providers.add(fsCacheDataProvider);

        dataLoader.setProviders(providers);

        dataLoader.setListener(new DataLoader.LoadListener<User>()
        {
            @Override
            public void onLoadingFinished(int status)
            {
                System.out.println("finished:" + status);
            }

            @Override
            public void onDataLoaded(DataLoader.Result<User> result)
            {
                System.out.println("loaded:" + result.data + ", provider class: " + result.provider);
            }

            @Override
            public void onLoadStarted()
            {
                System.out.println("started");
            }
        });
        dataLoader.loadData();
    }

    public static class User
    {
        private String firstName, lastName;
        private int age;
        List<Integer> ids;

        public User()
        {
            ids = new ArrayList<>();
            ids.add(6);
            ids.add(7);
        }

        public String getFirstName()
        {
            return firstName;
        }

        public void setFirstName(String firstName)
        {
            this.firstName = firstName;
        }

        public String getLastName()
        {
            return lastName;
        }

        public void setLastName(String lastName)
        {
            this.lastName = lastName;
        }

        public int getAge()
        {
            return age;
        }

        public void setAge(int age)
        {
            this.age = age;
        }

        @Override
        public String toString()
        {
            return "User{" +
                    "firstName='" + firstName + '\'' +
                    ", lastName='" + lastName + '\'' +
                    ", age=" + age +
                    ", ids=" + ids +
                    '}';
        }
    }
}

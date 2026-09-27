    package com.example.springbootlearning.controller;

    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.PathVariable;
    import org.springframework.web.bind.annotation.PostMapping;
    import org.springframework.web.bind.annotation.RequestBody;
    import org.springframework.web.bind.annotation.RequestMapping;
    import org.springframework.web.bind.annotation.RequestParam;
    import org.springframework.web.bind.annotation.RestController;

    import com.example.springbootlearning.common.Result;
    import com.example.springbootlearning.dto.UserDTO;
    import com.example.springbootlearning.entity.User;
    import com.example.springbootlearning.service.UserService;

    import jakarta.validation.Valid;

    @RestController
    @RequestMapping("/users")
    public class UserController {

        // @GetMapping("/users")
        @GetMapping("/hello")       // 对应 GET /users/hello
        public String hello() {

            return "Hello User";

        }

        //无请求参数
        // @GetMapping                 //对应 GET /users
        // public String getUsers() {
        //     return "获取用户列表";
        // }

        // 1个同名参数
        @GetMapping                    // 上面也是@GetMapping 是同一个路径,不能重复 
        public String getUser(@RequestParam Long id) {   //  /users?id=1
            return "用户 ID：" + id;
        }

        // 1个不同名参数
        // @GetMapping
        // public String getUser(
        //         @RequestParam("user_id") Long id            //  /users?user_id=100
        // ) {
        //     return "用户 ID：" + id;
        // }

        // 2个同名参数
        // @GetMapping
        // public String getUser(
        //         @RequestParam Long id,
        //         @RequestParam String name
        // ) {
        //     return "ID：" + id + "，姓名：" + name;
        // }

        // 让参数变成可选 @RequestParam(required = false) Long id
        // 设置默认值 @RequestParam(defaultValue = "1") Long page
        // @GetMapping
        // public String getUser(
        //         @RequestParam(defaultValue = "1") Long page
        // ) {
        //     return "当前页：" + page;
        // }
        

        //单路径参数
        // @GetMapping("/{id}")
        // public String getUserById(
        //         @PathVariable Long id
        // ) {
        //     return "用户 ID：" + id;
        // }

        // 多个路径参数
        // @GetMapping("/{userId}/orders/{orderId}")
        // public String getOrder(
        //         @PathVariable Long userId,
        //         @PathVariable Long orderId
        // ) {
        //     return "用户 ID：" + userId
        //             + "，订单 ID：" + orderId;
        // }


        // 第三种参数：@RequestBody 
        // @PostMapping
        // public String createUser(
        //         @RequestBody User user
        // ) {
        //     return "用户名：" + user.getName()
        //             + "，年龄：" + user.getAge();
        // }

        // 依赖注入中的构造器注入
        private final UserService userService;

        public UserController(UserService userService) {
            this.userService = userService;
        }

        
        @GetMapping("/info")
        public String getUserInfo() {
            return userService.getUser();
        }
        
        //
        @GetMapping("/{id}")
        public Result<User> getUserById(
                @PathVariable Long id
        ) {
            User user = userService.getUserById(id);
            // User user =null;
            // if (user == null) {
            //     return Result.error(
            //             404,
            //             "用户不存在"
            //     );
            // }  后面为了分层架构, controller不负责处理这个

            // return new Result<>(  //有了静态成功方法就可以不这样写了
            //         200,
            //         "success",
            //         user
            // );
            return Result.success(user);
        }

        @PostMapping
        public Result<Void> createUser(  @Valid @RequestBody UserDTO user) {
            //
            return Result.success(null);
        }


    }

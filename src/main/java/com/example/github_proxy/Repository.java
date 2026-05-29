package com.example.github_proxy;

import java.util.List;

record Repository(
        String name,
        String ownerLogin,
        List<Branch> branches
) {}

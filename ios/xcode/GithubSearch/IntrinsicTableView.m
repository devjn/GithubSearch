//
//  IntrinsicTableView.m
//  GithubSearch
//
//  Created by Admin on 20.05.17.
//  Copyright © 2017 devjn. All rights reserved.
//

#import "IntrinsicTableView.h"

@implementation IntrinsicTableView

- (void)setContentSize:(CGSize)contentSize {
    [super setContentSize:contentSize];
    [self invalidateIntrinsicContentSize];
}

- (CGSize)intrinsicContentSize {
    [self layoutIfNeeded]; // force my contentSize to be updated immediately
    return CGSizeMake(UIViewNoIntrinsicMetric, self.contentSize.height);
}

@end

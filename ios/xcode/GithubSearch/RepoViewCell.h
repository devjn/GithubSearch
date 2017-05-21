//
//  RepoViewCell.h
//  GithubSearch
//
//  Created by Admin on 18.05.17.
//  Copyright © 2017 devjn. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface RepoViewCell : UITableViewCell
@property (weak, nonatomic) IBOutlet UILabel *titleLabel;
@property (weak, nonatomic) IBOutlet UILabel *descriptionLabel;
@property (weak, nonatomic) IBOutlet UILabel *langLabel;

@end

//
//  UserDetailsController.h
//  GithubSearch
//
//  Created by Admin on 13.05.17.
//  Copyright © 2017 devjn. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface UserDetailsController : UIViewController

@property (weak, nonatomic) IBOutlet UIView *contentView;
@property (strong, nonatomic) IBOutlet UITextView *textLogin;
@property (weak, nonatomic) IBOutlet UIImageView *imageView;
@property (weak, nonatomic) IBOutlet UITextView *textName;
@property (weak, nonatomic) IBOutlet UIStackView *mainStack;

@property (weak, nonatomic) IBOutlet UIStackView *infoView;
@property (weak, nonatomic) IBOutlet UITextView *textBio;
@property (weak, nonatomic) IBOutlet UITextView *textCompany;
@property (weak, nonatomic) IBOutlet UITextView *textLocation;
@property (weak, nonatomic) IBOutlet UITextView *textEmail;
@property (weak, nonatomic) IBOutlet UITextView *textBlog;
@property (weak, nonatomic) IBOutlet UITextView *textEmpty;
@property (weak, nonatomic) IBOutlet UITableView *tableView;

@end

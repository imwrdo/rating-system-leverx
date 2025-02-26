# Rating System

## Project Description
The goal of the project is to provide an independent rating system for sellers of in-game items (CS:GO, FIFA, Dota, Team Fortress, etc.). The rating is based on comments submitted by users, which are thoroughly verified by trusted individuals. These ratings form the basis for the overall top sellers in various game categories.

## User Roles
- Administrator
- Seller
- Anonymous User

## User Scenarios
1. **Seller Registration**
   - Seller visits the site and fills out a form to create their profile
   - Administrator reviews the information and approves/declines the request

2. **Submitting a Comment**
   - Anonymous User views a Seller's profile and leaves a comment
   - Administrator verifies the comment and approves/declines it

3. **Creating a Seller Profile via Comment**
   - If an Anonymous User doesn't find the seller they want to Comment, they can provide additional information to create the Seller's profile
   - Administrator reviews the submission and approves/declines it

## Core Functionality
- Creating Seller Profiles
- Submitting Comments for Sellers
- Calculating Seller Ratings
- Compiling Overall Top Sellers Based on Ratings
- Filtering by Games and Rating Ranges

## Registration and Authorization
### Process
1) User enters the required registration details.
2) The system generates a confirmation link (code), stores it in some Cache (Redis, for example), and sends it to the provided email.
3) The confirmation codes have a 24-hour expiration period.
4) Until the email is confirmed, any login attempts will result in an appropriate error message.

## Data Models

### User Model
```json
{
  "id": "Integer/UID",
  "first_name": "String",
  "last_name": "String",
  "password": "String",
  "email": "String",
  "created_at": "Date",
  "role": "Enum"
}
```

#### Password Reset Endpoints
Additionally Seller should have the ability to reset their password through the standard process:

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/forgot_password` | Submit {email} to receive a reset code via email |
| POST | `/auth/reset` | Submit {code, new_password}; the system verifies the code from Redis and, if valid, sets the new password. |
| GET | `/auth/check_code` | Verify the validity of the reset code and respond accordingly. |

### Comment Model
```json
{
  "id": "Integer/UID",
  "message": "Text",
  "author_id": "Integer/UID",
  "created_at": "Date",
  "approved": "Boolean"
}
```

#### Comment Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/users/:id/comments` | Add a comment linked to a user |
| GET | `/users/:id/comments` | List of seller's comments |
| GET | `/users/:id/comments/:id` | View a specific comment |
| DELETE | `/users/:id/comments/:id` | Delete a comment (only the author can delete) |
| PUT | `/users/:id/comments` | Update comment |

### Game Object Model
```json
{
  "id": "Integer/UID",
  "title": "String",
  "text": "Text",
  "user_id": "Integer/UID",
  "created_at": "Date",
  "updated_at": "Date"
}
```

#### Game Object Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| PUT | `/object/:id` | Edit an object (only the author can edit) |
| POST | `/object` | Add a new object |
| GET | `/object` | Retrieve game objects |
| DELETE | `/object/:id` | Delete an object (only the author can delete) |

## Testing Requirements
- Set up testing environment
- Create two unit tests
- Create two integration tests

## Development Stages
1. **Database Structure Design**
   - Plan and create the database schema. Attach it to the project as an image or PDF file.

2. **Project Architecture**
   - Design the project's architecture, create the folder and file structure.

3. **Development Planning**
   - Divide the project into development phases and provide rough time estimates for each phase. 
   - Create a file named estimate.md and describe all phases and their durations. Example:

      * Registration: 8h
      * Authorisation: 8h
      * ... 

4. **Development**
   - Make regular commits
   - Submit pull request upon completion